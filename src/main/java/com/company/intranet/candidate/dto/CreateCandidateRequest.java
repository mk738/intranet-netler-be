package com.company.intranet.candidate.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateCandidateRequest(
        @NotBlank String name,
        @NotBlank String role,
        String email,
        String phone,
        String notes,
        @Min(0) @Max(5) int stage
) {}
