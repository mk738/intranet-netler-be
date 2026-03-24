package com.company.intranet.crm.dto;

import com.company.intranet.crm.Client;
import jakarta.validation.constraints.NotBlank;

public record NewClientDto(
        @NotBlank String companyName,
        String           orgNumber,
        String           contactName,
        String           contactEmail,
        String           phone,
        Client.ClientStatus status
) {}
