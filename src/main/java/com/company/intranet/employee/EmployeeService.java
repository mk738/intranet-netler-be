package com.company.intranet.employee;

import com.company.intranet.common.exception.AppException;
import com.company.intranet.common.exception.ErrorCode;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.crm.AssignmentRepository;
import com.company.intranet.crm.CrmMapper;
import com.company.intranet.skill.Skill;
import com.company.intranet.skill.SkillService;
import com.company.intranet.employee.dto.*;
import com.company.intranet.notification.events.EmployeeInvitedEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private static final long    MAX_FILE_BYTES    = 10 * 1024 * 1024; // 10 MB
    private static final String  ALLOWED_MIME_TYPE = "application/pdf";
    private static final Pattern CLEARING_PATTERN  = Pattern.compile("^\\d{4}(-\\d{1,2})?$");
    private static final Pattern ACCOUNT_PATTERN   = Pattern.compile("^\\d{7,10}$");

    private final EmployeeRepository         employeeRepository;
    private final EducationRepository        educationRepository;
    private final BankInfoRepository         bankInfoRepository;
    private final EmployeeContractRepository contractRepository;
    private final EmployeeCvRepository       cvRepository;
    private final EmployeeBenefitRepository  benefitRepository;
    private final AssignmentRepository       assignmentRepository;
    private final CrmMapper                  crmMapper;
    private final SkillService               skillService;
    private final FirebaseAuth               firebaseAuth;
    private final ApplicationEventPublisher  eventPublisher;
    private final EmployeeMapper             employeeMapper;

    // ── Admin ─────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        return employeeRepository.findAllActiveWithProfile().stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeDetailDto getEmployeeById(UUID id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));

        List<EducationDto> education = employeeMapper.toEducationDtos(
                educationRepository.findByEmployeeOrderByStartYearDesc(employee));

        BankInfoDto bankInfo = bankInfoRepository.findByEmployee(employee)
                .map(employeeMapper::toBankInfoDto)
                .orElse(null);

        List<com.company.intranet.crm.dto.AssignmentDto> assignments = crmMapper.toAssignmentDtos(
                assignmentRepository.findByEmployeeOrderByStartDateDesc(employee));

        return employeeMapper.toDetailDto(employee, bankInfo, education, assignments);
    }

    /**
     * Invite flow:
     * 1. Validate email uniqueness.
     * 2. Create Firebase user — happens BEFORE the DB transaction so a DB failure
     *    does not leave a partial record without a matching Firebase account.
     * 3. Persist Employee + EmployeeProfile inside the @Transactional boundary.
     * 4. Generate the invite link and publish the event (fires after commit).
     */
    @Transactional
    public EmployeeDto inviteEmployee(InviteEmployeeRequest request) {
        if (employeeRepository.findByEmail(request.email()).isPresent()) {
            throw new AppException(
                    ErrorCode.EMPLOYEE_EMAIL_TAKEN,
                    "An employee with that email address already exists.",
                    HttpStatus.CONFLICT);
        }

        UserRecord userRecord;
        try {
            UserRecord.CreateRequest createReq = new UserRecord.CreateRequest()
                    .setEmail(request.email())
                    .setEmailVerified(false)
                    .setDisplayName(request.firstName() + " " + request.lastName());
            userRecord = firebaseAuth.createUser(createReq);
        } catch (FirebaseAuthException e) {
            throw new AppException(
                    ErrorCode.EMPLOYEE_EMAIL_TAKEN,
                    "Failed to create Firebase user: " + e.getMessage(),
                    HttpStatus.CONFLICT);
        }

        Employee savedEmployee;
        try {
            Employee employee = Employee.builder()
                    .firebaseUid(userRecord.getUid())
                    .email(request.email())
                    .role(request.role())
                    .isActive(true)
                    .build();

            employee = employeeRepository.save(employee);

            EmployeeProfile profile = EmployeeProfile.builder()
                    .employee(employee)
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .jobTitle(request.jobTitle())
                    .startDate(request.startDate())
                    .build();

            employee.setProfile(profile);
            savedEmployee = employeeRepository.save(employee);

        } catch (Exception e) {
            log.error("DB save failed after Firebase user creation. Orphaned Firebase UID: {}",
                    userRecord.getUid(), e);
            throw e;
        }

        String inviteLink = "";
        try {
            inviteLink = firebaseAuth.generatePasswordResetLink(request.email());
        } catch (FirebaseAuthException e) {
            log.warn("Failed to generate invite link for {}: {}", request.email(), e.getMessage());
        }

        eventPublisher.publishEvent(new EmployeeInvitedEvent(
                request.email(),
                request.firstName() + " " + request.lastName(),
                inviteLink));

        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto updateEmployeeProfile(UUID id, UpdateProfileRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));

        applyProfileUpdate(employee.getProfile(), request, true);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    // ── Self-service ──────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public EmployeeDetailDto getMyProfile(Employee me) {
        return getEmployeeById(me.getId());
    }

    @Transactional
    public EmployeeDto updateMyProfile(UpdateProfileRequest request, Employee me) {
        Employee employee = employeeRepository.findById(me.getId())
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));

        applyProfileUpdate(employee.getProfile(), request, false);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @Transactional
    public void updateMyBank(UpdateBankRequest request, Employee me) {
        if (!CLEARING_PATTERN.matcher(request.clearingNumber()).matches()) {
            throw new AppException(
                    ErrorCode.BANK_INVALID_CLEARING,
                    "Invalid clearing number format. Expected 4 digits, optionally followed by a hyphen and 1–2 digits (e.g. 8327-9).",
                    HttpStatus.BAD_REQUEST);
        }
        if (!ACCOUNT_PATTERN.matcher(request.accountNumber()).matches()) {
            throw new AppException(
                    ErrorCode.BANK_INVALID_ACCOUNT,
                    "Invalid account number format. Expected 7–10 digits.",
                    HttpStatus.BAD_REQUEST);
        }

        BankInfo bankInfo = bankInfoRepository.findByEmployee(me)
                .orElseGet(() -> BankInfo.builder().employee(me).build());

        bankInfo.setBankName(request.bankName());
        bankInfo.setAccountNumber(request.accountNumber());
        bankInfo.setClearingNumber(request.clearingNumber());

        bankInfoRepository.save(bankInfo);
    }

    @Transactional
    public EducationDto addEducation(AddEducationRequest request, Employee me) {
        Education education = employeeMapper.toEducation(request, me);
        return employeeMapper.toEducationDto(educationRepository.save(education));
    }

    @Transactional
    public void deleteEducation(UUID educationId, Employee me) {
        Education education = educationRepository.findById(educationId)
                .filter(e -> e.getEmployee().getId().equals(me.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Education entry not found"));
        educationRepository.delete(education);
    }

    // ── Contract ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ContractDto getContract(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));
        EmployeeContract contract = contractRepository.findByEmployee(employee)
                .orElseThrow(() -> new ResourceNotFoundException("No contract found"));
        return new ContractDto(
                Base64.getEncoder().encodeToString(contract.getData()),
                contract.getContentType()
        );
    }

    @Transactional
    public void uploadContract(UUID employeeId, MultipartFile file) {
        validateFile(file);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));
        try {
            EmployeeContract contract = contractRepository.findByEmployee(employee)
                    .orElseGet(() -> EmployeeContract.builder().employee(employee).build());
            contract.setContentType(file.getContentType());
            contract.setData(file.getBytes());
            contractRepository.save(contract);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE, "Failed to read uploaded file", HttpStatus.BAD_REQUEST);
        }
    }

    // ── CV ────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ContractDto getCv(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));
        EmployeeCv cv = cvRepository.findByEmployee(employee)
                .orElseThrow(() -> new ResourceNotFoundException("No CV found"));
        return new ContractDto(
                Base64.getEncoder().encodeToString(cv.getData()),
                cv.getContentType()
        );
    }

    @Transactional
    public void uploadCv(UUID employeeId, MultipartFile file) {
        validateFile(file);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));
        try {
            EmployeeCv cv = cvRepository.findByEmployee(employee)
                    .orElseGet(() -> EmployeeCv.builder().employee(employee).build());
            cv.setContentType(file.getContentType());
            cv.setData(file.getBytes());
            cvRepository.save(cv);
        } catch (IOException e) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE, "Failed to read uploaded file", HttpStatus.BAD_REQUEST);
        }
    }

    // ── Skills ────────────────────────────────────────────────────────────────

    @Transactional
    public EmployeeDto updateSkills(UUID id, UpdateSkillsRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));

        List<String> names = request.names().stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        if (names.size() > 50) {
            throw new AppException(
                    ErrorCode.EMPLOYEE_SKILLS_TOO_MANY,
                    "An employee may have at most 50 skills.",
                    HttpStatus.BAD_REQUEST);
        }
        names.forEach(name -> {
            if (name.length() > 60) {
                throw new AppException(
                        ErrorCode.EMPLOYEE_SKILL_TOO_LONG,
                        "Each skill may be at most 60 characters.",
                        HttpStatus.BAD_REQUEST);
            }
        });

        List<Skill> resolved = skillService.resolveSkills(names);
        employee.getSkills().clear();
        employee.getSkills().addAll(resolved);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    // ── Benefits ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BenefitDto> getBenefits(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));
        return benefitRepository.findByEmployeeOrderBySortOrderAsc(employee).stream()
                .map(b -> new BenefitDto(b.getId(), b.getName(), b.getDescription()))
                .toList();
    }

    @Transactional
    public List<BenefitDto> replaceBenefits(UUID employeeId, List<BenefitRequest> requests) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new AppException(
                        ErrorCode.EMPLOYEE_NOT_FOUND,
                        "Employee not found",
                        HttpStatus.NOT_FOUND));
        benefitRepository.deleteByEmployee(employee);
        benefitRepository.flush();
        List<EmployeeBenefit> saved = new java.util.ArrayList<>();
        for (int i = 0; i < requests.size(); i++) {
            BenefitRequest req = requests.get(i);
            saved.add(benefitRepository.save(EmployeeBenefit.builder()
                    .employee(employee)
                    .name(req.name())
                    .description(req.description())
                    .sortOrder(i)
                    .build()));
        }
        return saved.stream()
                .map(b -> new BenefitDto(b.getId(), b.getName(), b.getDescription()))
                .toList();
    }

    // ── Shared helpers ────────────────────────────────────────────────────────

    private void validateFile(MultipartFile file) {
        if (file.getSize() > MAX_FILE_BYTES) {
            throw new AppException(
                    ErrorCode.FILE_TOO_LARGE,
                    "File exceeds the maximum allowed size of 10 MB.",
                    HttpStatus.BAD_REQUEST);
        }
        if (!ALLOWED_MIME_TYPE.equals(file.getContentType())) {
            throw new AppException(
                    ErrorCode.FILE_INVALID_TYPE,
                    "Only PDF files are accepted (application/pdf).",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void applyProfileUpdate(EmployeeProfile profile,
                                    UpdateProfileRequest request,
                                    boolean updateStartDate) {
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setJobTitle(request.jobTitle());
        profile.setPhone(request.phone());
        profile.setAddress(request.address());
        profile.setEmergencyContact(request.emergencyContact());
        profile.setBirthDate(request.birthDate());
        if (updateStartDate) {
            profile.setStartDate(request.startDate());
        }
    }
}
