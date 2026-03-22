package com.company.intranet.skill.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AddSkillsRequest(
        @NotNull List<String> names
) {}
