package com.company.intranet.employee.dto;

import com.company.intranet.crm.dto.AssignmentDto;

import java.util.List;
import java.util.UUID;

public record EmployeeDetailDto(
        UUID id,
        String email,
        String role,
        boolean isActive,
        String createdAt,
        List<String> skills,
        EmployeeProfileDto profile,
        BankInfoDto bankInfo,
        List<EducationDto> education,
        List<AssignmentDto> assignments
) {}
