package com.company.intranet.employee.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record TerminateEmployeeRequest(
        @NotNull LocalDate terminationDate
) {}
