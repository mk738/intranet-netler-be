package com.company.intranet.candidate.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record PatchCandidateRequest(
        String name,
        String role,
        String email,
        String phone,
        String notes,
        @Min(0) @Max(5) Integer stage
) {}
