package com.company.intranet.vacation.dto;

import java.time.LocalDate;
import java.util.UUID;

public record VacationDto(
        UUID      id,
        UUID      employeeId,
        String    employeeName,
        String    employeeInitials,
        LocalDate startDate,
        LocalDate endDate,
        int       daysCount,
        String    status,
        String    reviewedBy,    // full name of reviewer, null if not yet reviewed
        String    reviewedAt,    // ISO instant string, null if not reviewed
        String    createdAt
) {}
