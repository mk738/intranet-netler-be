package com.company.intranet.notification.events;

import java.util.List;

public record VacationRequestedEvent(
        String employeeName,
        String employeeEmail,
        String jobTitle,
        String startDate,
        String endDate,
        int daysCount,
        String submittedAt,
        String dateRange,
        List<String> adminEmails
) {
}
