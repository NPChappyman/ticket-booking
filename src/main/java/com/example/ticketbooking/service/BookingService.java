package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.booking.BookingRequest;
import com.example.ticketbooking.dto.booking.BookingResponse;
import com.example.ticketbooking.dto.booking.BookingSeatResponse;
import com.example.ticketbooking.entity.*;
import com.example.ticketbooking.exception.ConflictException;
import com.example.ticketbooking.exception.NotFoundException;
import com.example.ticketbooking.repository.BookingRepository;
import com.example.ticketbooking.repository.SeatRepository;
import com.example.ticketbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final EventSessionService eventSessionService;

    @Value("${booking.hold-minutes:15}")
    private long holdMinutes;

    @Transactional
    public BookingResponse create(BookingRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: id=" + request.userId()));

        EventSession session = eventSessionService.getSessionOrThrow(request.sessionId());

        if (session.getStartsAt().isBefore(Instant.now())) {
            throw new ConflictException("Сеанс уже прошёл, бронирование недоступно");
        }

        // убираем дубли id мест, сохраняя порядок
        Set<Long> seatIds = new LinkedHashSet<>(request.seatIds());

        List<Seat> seats = seatRepository.findByIdInAndVenueId(List.copyOf(seatIds), session.getVenue().getId());
        if (seats.size() != seatIds.size()) {
            throw new NotFoundException("Одно или несколько мест не найдены на площадке этого сеанса");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setEventSession(session);
        booking.setStatus(BookingStatus.PENDING);
        booking.setExpiresAt(Instant.now().plus(holdMinutes, ChronoUnit.MINUTES));

        BigDecimal total = BigDecimal.ZERO;
        for (Seat seat : seats) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setSeat(seat);
            bookingSeat.setEventSession(session);
            bookingSeat.setPrice(session.getPrice());
            bookingSeat.setActive(true);
            booking.addSeat(bookingSeat);
            total = total.add(session.getPrice());
        }
        booking.setTotalPrice(total);

        try {
            // flush сразу, чтобы поймать нарушение уникального индекса здесь, а не в конце транзакции
            Booking saved = bookingRepository.saveAndFlush(booking);
            return toResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Одно или несколько выбранных мест уже забронированы на этот сеанс");
        }
    }

    public BookingResponse findById(Long id) {
        return toResponse(getBookingOrThrow(id));
    }

    public List<BookingResponse> findByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    /** Оплата брони (заглушка вместо интеграции с платёжным провайдером). */
    @Transactional
    public BookingResponse pay(Long id, Long userId) {
        Booking booking = getBookingOrThrow(id);
        requireOwner(booking, userId);

        if (booking.getStatus() == BookingStatus.EXPIRED
                || (booking.getStatus() == BookingStatus.PENDING && booking.getExpiresAt().isBefore(Instant.now()))) {
            booking.setStatus(BookingStatus.EXPIRED);
            booking.releaseSeats();
            throw new ConflictException("Срок брони истёк, оплата невозможна");
        }
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException("Оплатить можно только бронь в статусе PENDING, текущий статус: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.PAID);
        return toResponse(booking);
    }

    @Transactional
    public BookingResponse cancel(Long id, Long userId) {
        Booking booking = getBookingOrThrow(id);
        requireOwner(booking, userId);

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new ConflictException("Бронь уже в конечном статусе: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.releaseSeats();
        return toResponse(booking);
    }

    private void requireOwner(Booking booking, Long userId) {
        if (!booking.getUser().getId().equals(userId)) {
            throw new NotFoundException("Бронь не найдена: id=" + booking.getId());
        }
    }

    private Booking getBookingOrThrow(Long id) {
        return bookingRepository.findWithSeatsById(id)
                .orElseThrow(() -> new NotFoundException("Бронь не найдена: id=" + id));
    }

    private BookingResponse toResponse(Booking booking) {
        List<BookingSeatResponse> seats = booking.getSeats().stream()
                .filter(BookingSeat::isActive)
                .map(bs -> new BookingSeatResponse(
                        bs.getSeat().getId(), bs.getSeat().getRowNum(), bs.getSeat().getSeatNum(), bs.getPrice()))
                .toList();

        return new BookingResponse(
                booking.getId(),
                booking.getUser().getId(),
                booking.getEventSession().getId(),
                booking.getEventSession().getEvent().getTitle(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getCreatedAt(),
                booking.getExpiresAt(),
                seats
        );
    }
}
