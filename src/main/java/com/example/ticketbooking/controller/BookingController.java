package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.booking.BookingRequest;
import com.example.ticketbooking.dto.booking.BookingResponse;
import com.example.ticketbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * ВРЕМЕННО: userId передаётся параметром запроса вместо того, чтобы браться из JWT.
 * Поменяется на шаге аутентификации на "текущего пользователя" из SecurityContext.
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
        BookingResponse created = bookingService.create(request);
        return ResponseEntity.created(URI.create("/api/bookings/" + created.id())).body(created);
    }

    @GetMapping("/{id}")
    public BookingResponse findById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @GetMapping
    public List<BookingResponse> findByUser(@RequestParam Long userId) {
        return bookingService.findByUser(userId);
    }

    @PostMapping("/{id}/pay")
    public BookingResponse pay(@PathVariable Long id, @RequestParam Long userId) {
        return bookingService.pay(id, userId);
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(@PathVariable Long id, @RequestParam Long userId) {
        return bookingService.cancel(id, userId);
    }
}
