package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.session.EventSessionRequest;
import com.example.ticketbooking.dto.session.EventSessionResponse;
import com.example.ticketbooking.service.EventSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class EventSessionController {

    private final EventSessionService sessionService;

    @PostMapping
    public ResponseEntity<EventSessionResponse> create(@Valid @RequestBody EventSessionRequest request) {
        EventSessionResponse created = sessionService.create(request);
        return ResponseEntity.created(URI.create("/api/sessions/" + created.id())).body(created);
    }

    @GetMapping
    public List<EventSessionResponse> findAll() {
        return sessionService.findAll();
    }

    @GetMapping("/{id}")
    public EventSessionResponse findById(@PathVariable Long id) {
        return sessionService.findById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        sessionService.delete(id);
    }
}
