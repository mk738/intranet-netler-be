package com.company.intranet.employee.dto;

import com.company.intranet.crm.dto.AssignmentDto;
import com.company.intranet.skill.dto.SkillDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EmployeeDetailDto(
        UUID id,
        String email,
        String role,
        boolean isActive,
        LocalDate terminationDate,
        LocalDate employmentEndDate,
        String createdAt,
        List<SkillDto> skills,
        EmployeeProfileDto profile,
        BankInfoDto bankInfo,
        List<EducationDto> education,
        List<BenefitDto> benefits,
        AssignmentDto currentAssignment,
        List<AssignmentDto> assignments
) {}
