package com.example.ticketbooking.dto.user;

import com.example.ticketbooking.entity.Role;

public record UserResponse(Long id, String email, String fullName, Role role) {
}
