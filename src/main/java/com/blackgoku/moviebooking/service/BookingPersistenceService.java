package com.blackgoku.moviebooking.service;

import com.blackgoku.moviebooking.domain.Booking;
import com.blackgoku.moviebooking.domain.BookingSeat;
import com.blackgoku.moviebooking.domain.BookingStatus;
import com.blackgoku.moviebooking.domain.PaymentStatus;
import com.blackgoku.moviebooking.dto.ConfirmBookingRequest;
import com.blackgoku.moviebooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Deliberately a SEPARATE bean from BookingService, not a private/protected method
 * on it. Spring's @Transactional works via a dynamic proxy that intercepts calls
 * coming from OUTSIDE the bean; a call from BookingService.confirmBooking() to
 * `this.persistBooking(...)` on the same instance would bypass that proxy entirely,
 * silently making the timeout (and the transaction itself) not apply. Routing
 * through a real collaborator bean like this one ensures the call goes through the
 * proxy and @Transactional actually takes effect.
 */
@Service
@RequiredArgsConstructor
public class BookingPersistenceService {

    private final BookingRepository bookingRepository;


    // timeout(seconds) is deliberately shorter than SeatHoldService's 15s confirm-extension
    // window, so the DB gives up and rolls back BEFORE the Redis hold could expire mid-write.
    @Transactional(timeout = 5)
    public Booking persistBooking(ConfirmBookingRequest request, BigDecimal amount, String paymentRef) {
        Booking booking = Booking.builder()
                .id(UUID.randomUUID().toString())
                .showId(request.showId())
                .userId(request.userId())
                .idempotencyKey(request.idempotencyKey())
                .totalAmount(amount)
                .status(BookingStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.CAPTURED)
                .paymentReference(paymentRef)
                .build();

        request.seatIds().forEach(seatId -> booking.addSeat(BookingSeat.builder().showId(request.showId()).seatId(seatId).build()));

        return bookingRepository.save(booking);
    }
}
