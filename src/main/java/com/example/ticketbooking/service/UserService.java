package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.user.UserRequest;
import com.example.ticketbooking.dto.user.UserResponse;
import com.example.ticketbooking.entity.Role;
import com.example.ticketbooking.entity.User;
import com.example.ticketbooking.exception.ConflictException;
import com.example.ticketbooking.exception.NotFoundException;
import com.example.ticketbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ConflictException("Пользователь с email " + request.email() + " уже существует");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setRole(Role.USER);
        // ВРЕМЕННО: без пароля, пока нет Spring Security
        user.setPasswordHash("N/A");
        return toResponse(userRepository.save(user));
    }

    public UserResponse findById(Long id) {
        return toResponse(getUserOrThrow(id));
    }

    User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + id));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
    }
}
