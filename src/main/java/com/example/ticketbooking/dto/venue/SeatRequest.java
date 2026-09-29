package com.example.ticketbooking.dto.venue;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SeatRequest(
        @NotNull @Min(1) Integer rowNum,
        @NotNull @Min(1) Integer seatNum
) {
}
