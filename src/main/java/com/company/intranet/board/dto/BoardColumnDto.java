package com.company.intranet.board.dto;

import java.util.List;
import java.util.UUID;

public record BoardColumnDto(
        UUID             id,
        String           title,
        int              colorIndex,
        int              position,
        List<BoardCardDto> cards
) {}
