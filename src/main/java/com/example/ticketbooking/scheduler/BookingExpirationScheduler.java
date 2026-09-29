package com.example.ticketbooking.scheduler;

import com.example.ticketbooking.entity.Booking;
import com.example.ticketbooking.entity.BookingStatus;
import com.example.ticketbooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Раз в минуту находит PENDING-брони с истёкшим сроком, переводит их в EXPIRED
 * и освобождает места (booking_seats.active = false), чтобы их снова можно было забронировать.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingExpirationScheduler {

    private final BookingRepository bookingRepository;

    // fixedRate считает от начала предыдущего запуска, а не от его окончания -
    // при коротких задачах разницы почти нет, но так интервал стабильнее
    @Scheduled(fixedRateString = "${booking.expiration-check-interval-ms:60000}")
    @Transactional
    public void expireStaleBookings() {
        List<Booking> stale = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING, Instant.now());
        if (stale.isEmpty()) {
            return;
        }

        for (Booking booking : stale) {
            booking.setStatus(BookingStatus.EXPIRED);
            booking.releaseSeats();
        }

        log.info("Истекло броней: {}", stale.size());
    }
}
