package com.company.intranet.board.dto;

import java.util.List;
import java.util.UUID;

public record BoardCardDto(
        UUID                   id,
        String                 title,
        String                 text,
        String                 category,
        String                 assignedTo,
        int                    position,
        String                 createdAt,
        int                    attachmentCount,
        List<BoardCommentDto>  comments
) {}
