package com.company.intranet.board.dto;

import java.util.UUID;

public record CardAttachmentDto(
        UUID id,
        String fileName,
        String contentType,
        String downloadUrl
) {
}
