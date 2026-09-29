package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.session.EventSessionRequest;
import com.example.ticketbooking.dto.session.EventSessionResponse;
import com.example.ticketbooking.entity.Event;
import com.example.ticketbooking.entity.EventSession;
import com.example.ticketbooking.entity.Venue;
import com.example.ticketbooking.exception.NotFoundException;
import com.example.ticketbooking.repository.EventRepository;
import com.example.ticketbooking.repository.EventSessionRepository;
import com.example.ticketbooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventSessionService {

    private final EventSessionRepository sessionRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public EventSessionResponse create(EventSessionRequest request) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + request.eventId()));
        Venue venue = venueRepository.findById(request.venueId())
                .orElseThrow(() -> new NotFoundException("Площадка не найдена: id=" + request.venueId()));

        EventSession session = new EventSession();
        session.setEvent(event);
        session.setVenue(venue);
        session.setStartsAt(request.startsAt());
        session.setPrice(request.price());
        return toResponse(sessionRepository.save(session));
    }

    public List<EventSessionResponse> findAll() {
        return sessionRepository.findAllBy().stream().map(this::toResponse).toList();
    }

    public List<EventSessionResponse> findByEvent(Long eventId) {
        return sessionRepository.findByEventId(eventId).stream().map(this::toResponse).toList();
    }

    public EventSessionResponse findById(Long id) {
        return toResponse(getSessionOrThrow(id));
    }

    @Transactional
    public void delete(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new NotFoundException("Сеанс не найден: id=" + id);
        }
        sessionRepository.deleteById(id);
    }

    EventSession getSessionOrThrow(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Сеанс не найден: id=" + id));
    }

    private EventSessionResponse toResponse(EventSession s) {
        return new EventSessionResponse(
                s.getId(),
                s.getEvent().getId(), s.getEvent().getTitle(),
                s.getVenue().getId(), s.getVenue().getName(),
                s.getStartsAt(), s.getPrice()
        );
    }
}
