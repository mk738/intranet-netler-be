package com.company.intranet.faq.dto;

import java.util.UUID;

public record FaqItemDto(
        UUID   id,
        String question,
        String answer,
        String category,
        int    sortOrder,
        String createdAt
) {}
