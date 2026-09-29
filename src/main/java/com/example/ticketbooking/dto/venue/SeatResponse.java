package com.example.ticketbooking.dto.venue;

public record SeatResponse(Long id, Long venueId, int rowNum, int seatNum) {
}
