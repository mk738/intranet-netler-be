package com.company.intranet.employee.dto;

import jakarta.validation.constraints.NotBlank;

public record BenefitRequest(
        @NotBlank String name,
        String description
) {}
