package com.company.intranet.employee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddEducationRequest(
        @NotBlank String institution,
        @NotBlank String degree,
        @NotBlank String field,
        @NotNull Integer startYear,
        Integer endYear,
        String description
) {}
