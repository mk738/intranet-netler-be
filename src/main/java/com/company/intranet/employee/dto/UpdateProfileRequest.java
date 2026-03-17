package com.company.intranet.employee.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdateProfileRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String jobTitle,
        String phone,
        String address,
        String emergencyContact,
        LocalDate birthDate,
        LocalDate startDate
) {}
