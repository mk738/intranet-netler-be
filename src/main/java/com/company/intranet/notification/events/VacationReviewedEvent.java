package com.company.intranet.notification.events;

public record VacationReviewedEvent(
        String employeeEmail,
        String employeeName,
        String dateRange,
        String status   // "approved" or "rejected"
) {}
