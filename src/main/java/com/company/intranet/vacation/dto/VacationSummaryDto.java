package com.company.intranet.vacation.dto;

public record VacationSummaryDto(
        long pending,
        long approved,
        long rejected
) {}
