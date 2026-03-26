package com.company.intranet.board.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(@NotBlank String text) {}
