package com.example.ticketbooking.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * ВРЕМЕННО: создание пользователя без пароля, пока нет Spring Security.
 * На шаге аутентификации это заменится на нормальную регистрацию с хешированием пароля.
 */
public record UserRequest(
        @NotBlank @Email String email,
        @NotBlank String fullName
) {
}
