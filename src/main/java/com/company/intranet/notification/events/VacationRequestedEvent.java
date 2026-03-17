package com.company.intranet.notification.events;

import java.util.List;

public record VacationRequestedEvent(
        String       employeeName,
        String       employeeEmail,
        String       dateRange,
        List<String> adminEmails
) {}
