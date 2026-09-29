package com.example.ticketbooking.dto.booking;

import java.math.BigDecimal;

public record BookingSeatResponse(Long seatId, int rowNum, int seatNum, BigDecimal price) {
}
