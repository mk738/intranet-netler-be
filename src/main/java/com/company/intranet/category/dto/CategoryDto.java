package com.company.intranet.category.dto;

import java.util.UUID;

public record CategoryDto(
        UUID   id,
        String name,
        String type,
        String createdAt
) {}
