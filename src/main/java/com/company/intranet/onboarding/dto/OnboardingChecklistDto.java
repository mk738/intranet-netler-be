package com.company.intranet.onboarding.dto;

import java.util.List;

public record OnboardingChecklistDto(
        boolean onboardingComplete,
        List<OnboardingItemDto> items
) {}
