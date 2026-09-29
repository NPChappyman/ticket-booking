package com.example.ticketbooking.dto.booking;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * ВРЕМЕННО: userId передаётся в теле запроса, потому что аутентификации ещё нет.
 * После шага Spring Security + JWT userId будет браться из токена, а не из тела.
 */
public record BookingRequest(
        @NotNull(message = "userId обязателен") Long userId,
        @NotNull(message = "sessionId обязателен") Long sessionId,
        @NotEmpty(message = "нужно выбрать хотя бы одно место") List<Long> seatIds
) {
}
