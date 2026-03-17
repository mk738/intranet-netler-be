package com.company.intranet.hub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateEventRequest(
        @NotBlank String    title,
        String              description,
        String              location,
        @NotNull LocalDate  eventDate,
        LocalDate           endDate,
        boolean             allDay
) {}
