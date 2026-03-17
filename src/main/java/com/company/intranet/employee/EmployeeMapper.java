package com.company.intranet.employee;

import com.company.intranet.employee.dto.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeMapper {

    public EmployeeDto toDto(Employee employee) {
        return new EmployeeDto(
                employee.getId(),
                employee.getEmail(),
                employee.getRole(),
                employee.isActive(),
                toProfileDto(employee.getProfile())
        );
    }

    public EmployeeProfileDto toProfileDto(EmployeeProfile profile) {
        if (profile == null) return null;
        return new EmployeeProfileDto(
                profile.getFirstName(),
                profile.getLastName(),
                profile.getJobTitle(),
                profile.getPhone(),
                profile.getAddress(),
                profile.getAvatarUrl(),
                profile.getStartDate(),
                profile.getBirthDate()
        );
    }

    public EmployeeDetailDto toDetailDto(Employee employee,
                                         BankInfoDto bankInfo,
                                         List<EducationDto> education) {
        return new EmployeeDetailDto(
                employee.getId(),
                employee.getEmail(),
                employee.getRole().name(),
                employee.isActive(),
                employee.getCreatedAt() != null ? employee.getCreatedAt().toString() : null,
                toProfileDto(employee.getProfile()),
                bankInfo,
                education
        );
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
}
