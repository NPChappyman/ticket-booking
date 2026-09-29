package com.example.ticketbooking.repository;

import com.example.ticketbooking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
