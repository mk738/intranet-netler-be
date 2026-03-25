package com.company.intranet.hub.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateNewsRequest(
        @NotBlank String title,
        @NotBlank String body,
        boolean pinned,
        boolean published,
        String category,
        String coverImageData,
        String coverImageType
) {}
