package com.example.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter @Setter @NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_session_id")
    private EventSession eventSession;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "total_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    // оптимистичная блокировка: параллельное изменение брони (оплата vs отмена) не пройдёт молча
    @Version
    private Long version;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> seats = new ArrayList<>();

    public void addSeat(BookingSeat bookingSeat) {
        seats.add(bookingSeat);
        bookingSeat.setBooking(this);
    }

    /** Освобождает места: используется при отмене и истечении брони. */
    public void releaseSeats() {
        seats.forEach(s -> s.setActive(false));
    }
}
