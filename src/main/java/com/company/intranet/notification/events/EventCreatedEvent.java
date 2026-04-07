package com.company.intranet.notification.events;

import java.util.List;

public record EventCreatedEvent(
        String eventTitle,
        String eventDate,
        String location,
        List<String> recipientEmails
) {
}
