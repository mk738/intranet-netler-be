package com.company.intranet.employee.dto;

import com.company.intranet.employee.Employee;

import java.util.UUID;

public record EmployeeDto(
        UUID id,
        String email,
        Employee.Role role,
        boolean isActive,
        EmployeeProfileDto profile
) {}
