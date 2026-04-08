package com.company.intranet.onboarding.dto;

import java.time.Instant;
import java.util.UUID;

public record OnboardingItemDto(
        UUID id,
        String task,
        boolean completed,
        Instant completedAt,
        String completedByName
) {}
