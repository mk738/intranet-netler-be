package com.company.intranet.onboarding.dto;

import java.util.UUID;

public record OnboardingTemplateItemDto(
        UUID    id,
        String  taskKey,
        String  labelSv,
        int     sortOrder,
        boolean active
) {}
