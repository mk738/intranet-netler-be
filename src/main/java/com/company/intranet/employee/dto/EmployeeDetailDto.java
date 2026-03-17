package com.company.intranet.employee.dto;

import java.util.List;
import java.util.UUID;

public record EmployeeDetailDto(
        UUID id,
        String email,
        String role,
        boolean isActive,
        String createdAt,
        EmployeeProfileDto profile,
        BankInfoDto bankInfo,
        List<EducationDto> education
) {}
