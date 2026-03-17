package com.company.intranet.employee.dto;

import com.company.intranet.employee.Employee;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record InviteEmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email String email,
        String jobTitle,
        @NotNull Employee.Role role,
        LocalDate startDate
) {}
