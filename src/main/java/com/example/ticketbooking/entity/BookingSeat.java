package com.example.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_seats")
@Getter @Setter @NoArgsConstructor
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    // дублируем сеанс, чтобы уникальный индекс (сеанс, место) работал на уровне БД
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_session_id")
    private EventSession eventSession;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;
}
