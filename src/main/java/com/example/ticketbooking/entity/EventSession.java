package com.example.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "event_sessions")
@Getter @Setter @NoArgsConstructor
public class EventSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id")
    private Venue venue;

    @Column(name = "starts_at", nullable = false)
    private Instant startsAt;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
}
