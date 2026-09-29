package com.example.ticketbooking.dto.session;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record EventSessionRequest(
        @NotNull(message = "событие обязательно") Long eventId,
        @NotNull(message = "площадка обязательна") Long venueId,
        @NotNull(message = "время начала обязательно")
        @Future(message = "сеанс должен быть в будущем") Instant startsAt,
        @NotNull(message = "цена обязательна")
        @DecimalMin(value = "0.0", message = "цена не может быть отрицательной") BigDecimal price
) {
}
