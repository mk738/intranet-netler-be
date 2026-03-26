package com.company.intranet.board.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCardRequest(
        @NotBlank String title,
        String text,
        String category,
        String assignedTo,
        int    position
) {}
