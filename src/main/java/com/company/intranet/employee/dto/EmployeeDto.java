package com.company.intranet.employee.dto;

import com.company.intranet.employee.Employee;

import java.util.List;
import java.util.UUID;

public record EmployeeDto(
        UUID id,
        String email,
        Employee.Role role,
        boolean isActive,
        List<String> skills,
        EmployeeProfileDto profile
) {}
