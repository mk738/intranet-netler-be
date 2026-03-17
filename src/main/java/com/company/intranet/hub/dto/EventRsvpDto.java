package com.company.intranet.hub.dto;

public record EventRsvpDto(
        String myRsvp,
        long   goingCount,
        long   maybeCount,
        long   notGoingCount
) {}
