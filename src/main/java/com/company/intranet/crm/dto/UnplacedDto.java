package com.company.intranet.crm.dto;

import java.util.UUID;

public record UnplacedDto(
        UUID   employeeId,
        String fullName,
        String initials,
        String jobTitle,
        String lastPlacedClient,  // null if never placed
        String lastPlacedDate     // ISO date string, null if never placed
) {}
