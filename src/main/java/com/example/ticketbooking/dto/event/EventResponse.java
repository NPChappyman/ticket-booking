package com.example.ticketbooking.dto.event;

import com.example.ticketbooking.entity.EventType;

import java.time.Instant;

public record EventResponse(
        Long id,
        String title,
        String description,
        EventType type,
        Instant createdAt
) {
}
