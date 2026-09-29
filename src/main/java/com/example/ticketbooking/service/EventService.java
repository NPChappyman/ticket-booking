package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.event.EventRequest;
import com.example.ticketbooking.dto.event.EventResponse;
import com.example.ticketbooking.entity.Event;
import com.example.ticketbooking.exception.NotFoundException;
import com.example.ticketbooking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;

    @Transactional
    public EventResponse create(EventRequest request) {
        Event event = new Event();
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setType(request.type());
        return toResponse(eventRepository.save(event));
    }

    public List<EventResponse> findAll() {
        return eventRepository.findAll().stream().map(this::toResponse).toList();
    }

    public EventResponse findById(Long id) {
        return toResponse(getEventOrThrow(id));
    }

    @Transactional
    public EventResponse update(Long id, EventRequest request) {
        Event event = getEventOrThrow(id);
        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setType(request.type());
        return toResponse(event);
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException("Событие не найдено: id=" + id);
        }
        eventRepository.deleteById(id);
    }

    Event getEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие не найдено: id=" + id));
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(event.getId(), event.getTitle(), event.getDescription(),
                event.getType(), event.getCreatedAt());
    }
}
