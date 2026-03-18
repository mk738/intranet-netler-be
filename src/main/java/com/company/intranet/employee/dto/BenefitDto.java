package com.company.intranet.employee.dto;

import java.util.UUID;

public record BenefitDto(
        UUID id,
        String name,
        String description
) {}
