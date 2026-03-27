package com.company.intranet.employee.dto;

import com.company.intranet.employee.Employee;
import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(@NotNull Employee.Role role) {}
