package com.example.ticketbooking.dto.session;

import java.math.BigDecimal;
import java.time.Instant;

public record EventSessionResponse(
        Long id,
        Long eventId,
        String eventTitle,
        Long venueId,
        String venueName,
        Instant startsAt,
        BigDecimal price
) {
}
