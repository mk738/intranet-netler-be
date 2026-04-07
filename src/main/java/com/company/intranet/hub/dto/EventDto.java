package com.company.intranet.hub.dto;

import java.time.LocalDate;
import java.util.UUID;

public record EventDto(
        UUID id,
        String title,
        String description,
        String location,
        LocalDate eventDate,
        LocalDate endDate,
        boolean allDay,
        String startTime,
        String endTime,
        String authorName,
        String createdAt,
        String myRsvpStatus   // null if the current user has not RSVPed
) {
}
