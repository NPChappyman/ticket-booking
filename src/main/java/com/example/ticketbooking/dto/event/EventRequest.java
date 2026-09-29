package com.example.ticketbooking.dto.event;

import com.example.ticketbooking.entity.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventRequest(
        @NotBlank(message = "название обязательно") String title,
        String description,
        @NotNull(message = "тип события обязателен") EventType type
) {
}
