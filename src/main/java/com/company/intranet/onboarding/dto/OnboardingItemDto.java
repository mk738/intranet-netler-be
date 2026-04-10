package com.company.intranet.onboarding.dto;

import java.time.Instant;
import java.util.UUID;

public record OnboardingItemDto(
        UUID id,
        String taskKey,
        String labelSv,
        int sortOrder,
        boolean completed,
        Instant completedAt,
        String completedByName
) {}
