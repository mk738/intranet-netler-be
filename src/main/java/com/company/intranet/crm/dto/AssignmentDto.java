package com.company.intranet.crm.dto;

import java.time.LocalDate;
import java.util.UUID;

public record AssignmentDto(
        UUID      id,
        UUID      employeeId,
        String    fullName,
        String    initials,
        String    jobTitle,
        UUID      clientId,
        String    companyName,
        String    projectName,
        LocalDate startDate,
        LocalDate endDate,
        String    status   // "ACTIVE" | "ENDING_SOON" | "ENDED" — ENDING_SOON is computed, never stored
) {}
