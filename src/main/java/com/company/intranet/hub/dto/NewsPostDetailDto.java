package com.company.intranet.hub.dto;

import java.time.Instant;
import java.util.UUID;

public record NewsPostDetailDto(
        UUID    id,
        String  title,
        String  body,
        String  authorName,
        String  authorInitials,
        Instant publishedAt,
        boolean pinned,
        String  coverImageData,
        String  coverImageType,
        Instant createdAt
) {}
