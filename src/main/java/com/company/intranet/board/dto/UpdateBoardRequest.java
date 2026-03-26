package com.company.intranet.board.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBoardRequest(@NotBlank String name) {}
