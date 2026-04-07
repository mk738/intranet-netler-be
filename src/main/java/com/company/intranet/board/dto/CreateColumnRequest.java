package com.company.intranet.board.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateColumnRequest(
        @NotBlank String title,
        int colorIndex,
        int position
) {
}
