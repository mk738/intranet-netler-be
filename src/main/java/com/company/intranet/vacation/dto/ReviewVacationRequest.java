package com.company.intranet.vacation.dto;

import jakarta.validation.constraints.NotNull;

public record ReviewVacationRequest(
        @NotNull Boolean approved,
        String rejectionReason
) {}
