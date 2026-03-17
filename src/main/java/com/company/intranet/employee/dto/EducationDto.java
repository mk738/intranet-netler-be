package com.company.intranet.employee.dto;

import java.util.UUID;

public record EducationDto(
        UUID id,
        String institution,
        String degree,
        String field,
        int startYear,
        Integer endYear,
        String description
) {}
