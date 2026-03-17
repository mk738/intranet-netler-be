package com.company.intranet.vacation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record SubmitVacationRequest(
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {}
