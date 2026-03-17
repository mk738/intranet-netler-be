package com.company.intranet.notification.events;

public record EmployeeInvitedEvent(
        String recipientEmail,
        String recipientName,
        String inviteLink
) {}
