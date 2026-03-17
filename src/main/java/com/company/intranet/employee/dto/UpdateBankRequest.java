package com.company.intranet.employee.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBankRequest(
        String bankName,
        @NotBlank String accountNumber,
        @NotBlank String clearingNumber
) {}
