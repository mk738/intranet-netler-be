package com.company.intranet.crm.dto;

import com.company.intranet.crm.Client;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateClientRequest(
        @NotBlank String companyName,
        String           contactName,
        String           contactEmail,
        String           phone,
        String           orgNumber,
        @NotNull Client.ClientStatus status
) {}
