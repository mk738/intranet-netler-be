package com.company.intranet.hub.dto;

import java.util.UUID;

public record NewsPostDto(
        UUID    id,
        String  title,
        String  authorName,
        String  authorInitials,
        String  publishedAt,
        boolean pinned,
        boolean hasImage,
        String  createdAt
) {}
