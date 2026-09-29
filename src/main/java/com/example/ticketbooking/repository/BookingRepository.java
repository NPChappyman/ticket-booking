package com.example.ticketbooking.repository;

import com.example.ticketbooking.entity.Booking;
import com.example.ticketbooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"user", "eventSession", "eventSession.event", "seats", "seats.seat"})
    Optional<Booking> findWithSeatsById(Long id);

    @EntityGraph(attributePaths = {"eventSession", "eventSession.event", "seats", "seats.seat"})
    List<Booking> findByUserId(Long userId);

    // используется планировщиком: находит зависшие PENDING-брони с истёкшим сроком
    List<Booking> findByStatusAndExpiresAtBefore(BookingStatus status, Instant instant);

    @Query("select coalesce(sum(bs.price), 0) from BookingSeat bs where bs.eventSession.id = :sessionId and bs.active = true")
    java.math.BigDecimal sumActiveSeatsPrice(Long sessionId);
}
