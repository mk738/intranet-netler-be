package com.company.intranet.onboarding.dto;

public record UpdateTemplateItemRequest(
        String  labelSv,
        Integer sortOrder,
        Boolean active
) {}
