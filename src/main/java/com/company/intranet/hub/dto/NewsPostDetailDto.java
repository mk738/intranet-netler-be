package com.company.intranet.hub.dto;

import java.util.UUID;

public record NewsPostDetailDto(
        UUID    id,
        String  title,
        String  body,
        String  authorName,
        String  authorInitials,
        String  publishedAt,
        boolean pinned,
        String  coverImageData,
        String  coverImageType,
        String  createdAt
) {}
