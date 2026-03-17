package com.company.intranet.hub.dto;

import com.company.intranet.hub.EventRsvp;
import jakarta.validation.constraints.NotNull;

public record RsvpRequest(@NotNull EventRsvp.RsvpStatus status) {}
