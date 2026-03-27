package com.company.intranet.employee.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateActiveRequest(@NotNull Boolean active) {}
