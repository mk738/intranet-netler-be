package com.company.intranet.hub.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EventDto(
        UUID      id,
        String    title,
        String    description,
        String    location,
        LocalDate eventDate,
        LocalDate endDate,
        boolean   allDay,
        String    authorName,
        String    createdAt
) {}
