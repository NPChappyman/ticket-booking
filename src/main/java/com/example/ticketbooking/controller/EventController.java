package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.event.EventRequest;
import com.example.ticketbooking.dto.event.EventResponse;
import com.example.ticketbooking.dto.session.EventSessionResponse;
import com.example.ticketbooking.service.EventService;
import com.example.ticketbooking.service.EventSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventSessionService sessionService;

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventRequest request) {
        EventResponse created = eventService.create(request);
        return ResponseEntity.created(URI.create("/api/events/" + created.id())).body(created);
    }

    @GetMapping
    public List<EventResponse> findAll() {
        return eventService.findAll();
    }

    @GetMapping("/{id}")
    public EventResponse findById(@PathVariable Long id) {
        return eventService.findById(id);
    }

    @PutMapping("/{id}")
    public EventResponse update(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return eventService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        eventService.delete(id);
    }

    // сеансы конкретного события: /api/events/{id}/sessions
    @GetMapping("/{id}/sessions")
    public List<EventSessionResponse> sessions(@PathVariable Long id) {
        return sessionService.findByEvent(id);
    }
}
