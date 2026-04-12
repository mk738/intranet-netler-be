package com.company.intranet.employee.dto;

import java.time.Instant;
import java.util.UUID;

public record EmployeeHardwareDto(
        UUID id,
        String name,
        Instant createdAt
) {}
