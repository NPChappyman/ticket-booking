package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.venue.SeatRequest;
import com.example.ticketbooking.dto.venue.SeatResponse;
import com.example.ticketbooking.dto.venue.VenueRequest;
import com.example.ticketbooking.dto.venue.VenueResponse;
import com.example.ticketbooking.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueRequest request) {
        VenueResponse created = venueService.create(request);
        return ResponseEntity.created(URI.create("/api/venues/" + created.id())).body(created);
    }

    @GetMapping
    public List<VenueResponse> findAll() {
        return venueService.findAll();
    }

    @GetMapping("/{id}")
    public VenueResponse findById(@PathVariable Long id) {
        return venueService.findById(id);
    }

    @PutMapping("/{id}")
    public VenueResponse update(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
        return venueService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        venueService.delete(id);
    }

    @PostMapping("/{id}/seats")
    public ResponseEntity<SeatResponse> addSeat(@PathVariable Long id, @Valid @RequestBody SeatRequest request) {
        SeatResponse created = venueService.addSeat(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}/seats")
    public List<SeatResponse> getSeats(@PathVariable Long id) {
        return venueService.getSeats(id);
    }
}
