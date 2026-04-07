package com.company.intranet.crm.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateAssignmentRequest(
        @NotNull UUID employeeId,
        UUID clientId,    // either clientId OR newClient — validated in service
        @Valid NewClientDto newClient,
        @NotBlank String projectName,
        @NotNull LocalDate startDate,
        LocalDate endDate
) {
}
