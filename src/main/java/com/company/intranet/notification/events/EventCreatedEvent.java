package com.company.intranet.notification.events;

import java.util.List;

public record EventCreatedEvent(
        String eventTitle,
        String eventDate,
        String startTime,
        String endTime,
        String location,
        String description,
        String authorName,
        List<String> recipientEmails
) {
}
