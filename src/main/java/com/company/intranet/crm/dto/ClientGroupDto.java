package com.company.intranet.crm.dto;

import java.util.List;
import java.util.UUID;

public record ClientGroupDto(
        UUID clientId,
        String companyName,
        String clientStatus,
        int assignmentCount,
        List<AssignmentDto> assignments
) {
}
