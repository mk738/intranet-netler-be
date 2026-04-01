package com.company.intranet.employee;

import com.company.intranet.storage.StorageProperties;
import com.company.intranet.storage.StorageService;
import com.company.intranet.crm.dto.AssignmentDto;
import com.company.intranet.employee.dto.*;
import com.company.intranet.skill.SkillService;
import com.company.intranet.skill.dto.SkillDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EmployeeMapper {

    private final SkillService              skillService;
    private final EmployeeAvatarRepository  avatarRepository;
    private final StorageService            storageService;
    private final StorageProperties         storageProps;

    public EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getEmail(),
                employee.getRole(),
                employee.isActive(),
                employee.getTerminationDate(),
                employee.getEmploymentEndDate(),
                toSkillDtos(employee),
                toProfileDto(employee.getProfile(), resolveAvatarUrl(employee))
        );
    }

    public EmployeeProfileDto toProfileDto(EmployeeProfile profile, String avatarUrl) {
        if (profile == null) return null;
        return new EmployeeProfileDto(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getJobTitle(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getEmergencyContact(),
                avatarUrl,
                profile.getStartDate(),
                profile.getBirthDate()
        );
    }

    public EmployeeDetailDto toDetailDto(Employee employee,
                                         BankInfoDto bankInfo,
                                         List<EducationDto> education,
                                         List<BenefitDto> benefits,
                                         List<AssignmentDto> assignments) {
        AssignmentDto currentAssignment = assignments.stream()
                .filter(a -> "ACTIVE".equals(a.status()) || "ENDING_SOON".equals(a.status()))
                .findFirst()
                .orElse(null);

        return new EmployeeDetailDto(
                employee.getId(),
                employee.getEmail(),
                employee.getRole().name(),
                employee.isActive(),
                employee.getTerminationDate(),
                employee.getEmploymentEndDate(),
                employee.getCreatedAt() != null ? employee.getCreatedAt().toString() : null,
                toSkillDtos(employee),
                toProfileDto(employee.getProfile(), resolveAvatarUrl(employee)),
                bankInfo,
                education,
                benefits,
                currentAssignment,
                assignments
        );
    }

    private String resolveAvatarUrl(Employee employee) {
        return avatarRepository.findByEmployee(employee)
                .map(EmployeeAvatar::getStoragePath)
                .filter(path -> path != null)
                .map(path -> storageService.getSignedUrl(storageProps.getBucket().getAvatars(), path))
                .orElse(null);
    }

    public BankInfoDto toBankInfoDto(BankInfo bankInfo) {
        if (bankInfo == null) return null;
        return new BankInfoDto(
                bankInfo.getBankName(),
                maskAccountNumber(bankInfo.getAccountNumber()),
                bankInfo.getClearingNumber()
        );
    }

    public String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) return accountNumber;
        return "••••" + accountNumber.substring(accountNumber.length() - 4);
    }

    public EducationDto toEducationDto(Education education) {
        return new EducationDto(
                education.getId(),
                education.getInstitution(),
                education.getDegree(),
                education.getField(),
                education.getStartYear(),
                education.getEndYear(),
                education.getDescription()
        );
    }

    public List<EducationDto> toEducationDtos(List<Education> educations) {
        return educations.stream()
                .map(this::toEducationDto)
                .toList();
    }

    public Education toEducation(AddEducationRequest request, Employee employee) {
        return Education.builder()
                .employee(employee)
                .institution(request.institution())
                .degree(request.degree())
                .field(request.field())
                .startYear(request.startYear())
                .endYear(request.endYear())
                .description(request.description())
                .build();
    }

    private List<SkillDto> toSkillDtos(Employee employee) {
        return employee.getSkills().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(skillService::toDto)
                .toList();
    }
}
