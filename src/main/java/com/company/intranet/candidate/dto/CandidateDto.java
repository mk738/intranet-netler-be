package com.company.intranet.candidate.dto;

import java.time.Instant;
import java.util.UUID;

public record CandidateDto(
        UUID    id,
        String  name,
        String  role,
        String  email,
        String  phone,
        String  notes,
        int     stage,
        Instant createdAt,
        Instant updatedAt
) {}
