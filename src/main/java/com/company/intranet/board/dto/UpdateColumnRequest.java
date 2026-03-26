package com.company.intranet.board.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateColumnRequest(
        @NotBlank String title,
        int colorIndex,
        int position
) {}
