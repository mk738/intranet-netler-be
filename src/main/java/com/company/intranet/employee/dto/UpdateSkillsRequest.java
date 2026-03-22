package com.company.intranet.employee.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateSkillsRequest(
        @NotNull List<String> skills
) {}
