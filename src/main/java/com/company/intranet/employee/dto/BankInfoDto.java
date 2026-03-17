package com.company.intranet.employee.dto;

public record BankInfoDto(
        String bankName,
        String accountNumber,
        String clearingNumber
) {}
