package com.example.ticketbooking.repository;

import com.example.ticketbooking.entity.EventSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventSessionRepository extends JpaRepository<EventSession, Long> {

    // fetch join event+venue одним запросом, чтобы не ловить N+1 при выводе списка сеансов
    @EntityGraph(attributePaths = {"event", "venue"})
    List<EventSession> findByEventId(Long eventId);

    @EntityGraph(attributePaths = {"event", "venue"})
    List<EventSession> findAllBy();
}
