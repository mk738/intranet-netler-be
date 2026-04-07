package com.company.intranet.board.dto;

import java.util.List;
import java.util.UUID;

public record BoardDto(
        UUID id,
        String name,
        String createdBy,
        String createdAt,
        List<BoardColumnDto> columns
) {
}
