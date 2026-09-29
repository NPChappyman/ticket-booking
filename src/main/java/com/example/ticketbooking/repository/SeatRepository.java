package com.example.ticketbooking.repository;

import com.example.ticketbooking.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByVenueId(Long venueId);
    boolean existsByVenueIdAndRowNumAndSeatNum(Long venueId, int rowNum, int seatNum);

    // все выбранные места должны существовать и принадлежать площадке сеанса
    List<Seat> findByIdInAndVenueId(List<Long> ids, Long venueId);
}
