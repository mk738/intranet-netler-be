package com.company.intranet.crm.dto;

import java.util.List;

public record PlacementViewDto(
        List<ClientGroupDto> clientGroups,
        List<UnplacedDto> unplaced,
        int totalPlaced,
        int totalUnplaced,
        int endingSoon,
        int totalActiveClients
) {
}
