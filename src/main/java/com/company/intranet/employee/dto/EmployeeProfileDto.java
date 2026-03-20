package com.company.intranet.employee.dto;

import java.time.LocalDate;

public record EmployeeProfileDto(
        String firstName,
        String lastName,
        String jobTitle,
        String phone,
        String address,
        String emergencyContact,
        String avatarUrl,
        LocalDate startDate,
        LocalDate birthDate
) {}
