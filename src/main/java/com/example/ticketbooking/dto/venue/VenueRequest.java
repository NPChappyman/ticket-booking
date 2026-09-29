package com.example.ticketbooking.dto.venue;

import jakarta.validation.constraints.NotBlank;

public record VenueRequest(
        @NotBlank(message = "название обязательно") String name,
        @NotBlank(message = "адрес обязателен") String address
) {
}
