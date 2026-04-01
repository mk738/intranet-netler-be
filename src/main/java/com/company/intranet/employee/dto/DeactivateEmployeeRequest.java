package com.company.intranet.employee.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record DeactivateEmployeeRequest(
        LocalDate employmentEndDate,
        @NotBlank String confirmName
) {}
