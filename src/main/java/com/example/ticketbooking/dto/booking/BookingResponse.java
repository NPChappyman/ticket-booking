package com.example.ticketbooking.dto.booking;

import com.example.ticketbooking.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record BookingResponse(
        Long id,
        Long userId,
        Long sessionId,
        String eventTitle,
        BookingStatus status,
        BigDecimal totalPrice,
        Instant createdAt,
        Instant expiresAt,
        List<BookingSeatResponse> seats
) {
}
