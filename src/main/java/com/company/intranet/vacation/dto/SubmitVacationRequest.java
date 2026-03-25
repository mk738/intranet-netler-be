package com.company.intranet.vacation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SubmitVacationRequest(
        @NotNull  LocalDate startDate,
        @NotNull  LocalDate endDate,
        @NotBlank String    reason
) {}
