package com.company.intranet.employee.dto;

import com.company.intranet.employee.Employee;
import com.company.intranet.skill.dto.SkillDto;

import java.util.List;
import java.util.UUID;

public record EmployeeDto(
        UUID id,
        String email,
        Employee.Role role,
        boolean isActive,
        List<SkillDto> skills,
        EmployeeProfileDto profile
) {}
