package com.company.intranet.notification.events;

import java.util.List;

public record NewsPublishedEvent(
        String       newsTitle,
        List<String> recipientEmails
) {}
