package com.company.intranet.crm.dto;

import java.util.UUID;

public record ClientDto(
        UUID   id,
        String companyName,
        String contactName,
        String contactEmail,
        String phone,
        String orgNumber,
        String status,
        String createdAt
) {}
