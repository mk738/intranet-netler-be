package com.company.intranet.board.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateCardRequest(
        @NotBlank String title,
        String text,
        String category,
        String assignedTo,
        int    position,
        UUID   columnId
) {}
