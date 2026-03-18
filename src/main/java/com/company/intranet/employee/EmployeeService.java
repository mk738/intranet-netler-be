package com.company.intranet.employee;

import com.company.intranet.common.exception.BadRequestException;
import com.company.intranet.common.exception.ResourceNotFoundException;
import com.company.intranet.employee.dto.*;
import com.company.intranet.notification.events.EmployeeInvitedEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final EmployeeRepository         employeeRepository;
    private final EducationRepository        educationRepository;
    private final BankInfoRepository         bankInfoRepository;
    private final EmployeeContractRepository contractRepository;
    private final EmployeeCvRepository       cvRepository;
    private final EmployeeBenefitRepository  benefitRepository;
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
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        List<EducationDto> education = employeeMapper.toEducationDtos(
                educationRepository.findByEmployeeOrderByStartYearDesc(employee));

        BankInfoDto bankInfo = bankInfoRepository.findByEmployee(employee)
                .map(employeeMapper::toBankInfoDto)
                .orElse(null);

        return employeeMapper.toDetailDto(employee, bankInfo, education);
    }

    /**
     * Invite flow:
     * 1. Validate email uniqueness (no transaction needed).
     * 2. Create Firebase user — happens BEFORE the DB transaction so a DB failure
     *    does not leave a partial record without a matching Firebase account.
     * 3. Persist Employee + EmployeeProfile inside the @Transactional boundary.
     * 4. Generate the invite link and publish the event (fires after commit).
     *
     * If step 3 fails after step 2 succeeded, the orphaned Firebase UID is logged
     * for manual cleanup.
     */
    @Transactional
    public EmployeeDto inviteEmployee(InviteEmployeeRequest request) {
        if (employeeRepository.findByEmail(request.email()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        // Step 2 — create Firebase user before opening the DB transaction window
        UserRecord userRecord;
        try {
            UserRecord.CreateRequest createReq = new UserRecord.CreateRequest()
                    .setEmail(request.email())
                    .setEmailVerified(false)
                    .setDisplayName(request.firstName() + " " + request.lastName());
            userRecord = firebaseAuth.createUser(createReq);
        } catch (FirebaseAuthException e) {
            throw new BadRequestException("Failed to create Firebase user: " + e.getMessage());
        }

        // Step 3 — persist; if this throws, log the orphaned Firebase UID
        Employee savedEmployee;
        try {
            Employee employee = Employee.builder()
                    .firebaseUid(userRecord.getUid())
                    .email(request.email())
                    .role(request.role())
                    .isActive(true)
                    .build();

            // Save first to obtain the generated id
            employee = employeeRepository.save(employee);

            EmployeeProfile profile = EmployeeProfile.builder()
                    .employee(employee)
                    .firstName(request.firstName())
                    .lastName(request.lastName())
                    .jobTitle(request.jobTitle())
                    .startDate(request.startDate())
                    .build();

            // Employee.profile has CascadeType.ALL — set both sides then re-save
            employee.setProfile(profile);
            savedEmployee = employeeRepository.save(employee);

        } catch (Exception e) {
            log.error("DB save failed after Firebase user creation. Orphaned Firebase UID: {}",
                    userRecord.getUid(), e);
            throw e;
        }

        // Step 4 — generate invite link (best-effort; employee already created)
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
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

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
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // startDate is intentionally excluded — employees cannot change their own start date
        applyProfileUpdate(employee.getProfile(), request, false);
        return employeeMapper.toDto(employeeRepository.save(employee));
    }

    @Transactional
    public void updateMyBank(UpdateBankRequest request, Employee me) {
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
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        EmployeeContract contract = contractRepository.findByEmployee(employee)
                .orElseThrow(() -> new ResourceNotFoundException("No contract found"));
        return new ContractDto(
                Base64.getEncoder().encodeToString(contract.getData()),
                contract.getContentType()
        );
    }

    @Transactional
    public void uploadContract(UUID employeeId, MultipartFile file) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        try {
            EmployeeContract contract = contractRepository.findByEmployee(employee)
                    .orElseGet(() -> EmployeeContract.builder().employee(employee).build());
            contract.setContentType(file.getContentType());
            contract.setData(file.getBytes());
            contractRepository.save(contract);
        } catch (IOException e) {
            throw new BadRequestException("Failed to read uploaded file");
        }
    }

    // ── CV ────────────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public ContractDto getCv(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        EmployeeCv cv = cvRepository.findByEmployee(employee)
                .orElseThrow(() -> new ResourceNotFoundException("No CV found"));
        return new ContractDto(
                Base64.getEncoder().encodeToString(cv.getData()),
                cv.getContentType()
        );
    }

    @Transactional
    public void uploadCv(UUID employeeId, MultipartFile file) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        try {
            EmployeeCv cv = cvRepository.findByEmployee(employee)
                    .orElseGet(() -> EmployeeCv.builder().employee(employee).build());
            cv.setContentType(file.getContentType());
            cv.setData(file.getBytes());
            cvRepository.save(cv);
        } catch (IOException e) {
            throw new BadRequestException("Failed to read uploaded file");
        }
    }

    // ── Benefits ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<BenefitDto> getBenefits(UUID employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return benefitRepository.findByEmployeeOrderBySortOrderAsc(employee).stream()
                .map(b -> new BenefitDto(b.getId(), b.getName(), b.getDescription()))
                .toList();
    }

    @Transactional
    public List<BenefitDto> replaceBenefits(UUID employeeId, List<BenefitRequest> requests) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
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
