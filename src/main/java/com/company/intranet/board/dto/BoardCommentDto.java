package com.company.intranet.board.dto;

import java.util.UUID;

public record BoardCommentDto(
        UUID id,
        String text,
        String authorName,
        String createdAt
) {
}
