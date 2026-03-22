package com.company.intranet.employee.dto;

import com.company.intranet.employee.Employee;
import com.company.intranet.skill.dto.SkillDto;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EmployeeDto(
        UUID id,
        String email,
        Employee.Role role,
        boolean isActive,
        LocalDate terminationDate,
        List<SkillDto> skills,
        EmployeeProfileDto profile
) {}
