package com.company.intranet.hub.dto;

import java.time.Instant;
import java.util.UUID;

public record NewsPostDto(
        UUID    id,
        String  title,
        String  authorName,
        String  authorInitials,
        Instant publishedAt,
        boolean pinned,
        boolean hasImage,
        Instant createdAt
) {}
