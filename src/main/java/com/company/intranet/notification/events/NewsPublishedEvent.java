package com.company.intranet.notification.events;

import java.util.List;

public record NewsPublishedEvent(
        java.util.UUID postId,
        String         newsTitle,
        String         authorName,
        String         publishedDate,
        String         excerpt,
        List<String>   recipientEmails
) {}
