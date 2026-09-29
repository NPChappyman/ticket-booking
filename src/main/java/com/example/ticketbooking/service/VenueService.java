package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.venue.SeatRequest;
import com.example.ticketbooking.dto.venue.SeatResponse;
import com.example.ticketbooking.dto.venue.VenueRequest;
import com.example.ticketbooking.dto.venue.VenueResponse;
import com.example.ticketbooking.entity.Seat;
import com.example.ticketbooking.entity.Venue;
import com.example.ticketbooking.exception.ConflictException;
import com.example.ticketbooking.exception.NotFoundException;
import com.example.ticketbooking.repository.SeatRepository;
import com.example.ticketbooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VenueService {

    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public VenueResponse create(VenueRequest request) {
        Venue venue = new Venue();
        venue.setName(request.name());
        venue.setAddress(request.address());
        return toResponse(venueRepository.save(venue));
    }

    public List<VenueResponse> findAll() {
        return venueRepository.findAll().stream().map(this::toResponse).toList();
    }

    public VenueResponse findById(Long id) {
        return toResponse(getVenueOrThrow(id));
    }

    @Transactional
    public VenueResponse update(Long id, VenueRequest request) {
        Venue venue = getVenueOrThrow(id);
        venue.setName(request.name());
        venue.setAddress(request.address());
        return toResponse(venue);
    }

    @Transactional
    public void delete(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new NotFoundException("Площадка не найдена: id=" + id);
        }
        venueRepository.deleteById(id);
    }

    @Transactional
    public SeatResponse addSeat(Long venueId, SeatRequest request) {
        Venue venue = getVenueOrThrow(venueId);
        if (seatRepository.existsByVenueIdAndRowNumAndSeatNum(venueId, request.rowNum(), request.seatNum())) {
            throw new ConflictException("Место row=%d, seat=%d уже существует на этой площадке"
                    .formatted(request.rowNum(), request.seatNum()));
        }
        Seat seat = new Seat();
        seat.setVenue(venue);
        seat.setRowNum(request.rowNum());
        seat.setSeatNum(request.seatNum());
        return toResponse(seatRepository.save(seat));
    }

    public List<SeatResponse> getSeats(Long venueId) {
        getVenueOrThrow(venueId);
        return seatRepository.findByVenueId(venueId).stream().map(this::toResponse).toList();
    }

    private Venue getVenueOrThrow(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Площадка не найдена: id=" + id));
    }

    private VenueResponse toResponse(Venue venue) {
        return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress());
    }

    private SeatResponse toResponse(Seat seat) {
        return new SeatResponse(seat.getId(), seat.getVenue().getId(), seat.getRowNum(), seat.getSeatNum());
    }
}
