package com.blackgoku.moviebooking.service;

import com.blackgoku.moviebooking.domain.Booking;
import com.blackgoku.moviebooking.domain.Show;
import com.blackgoku.moviebooking.dto.BookingResponse;
import com.blackgoku.moviebooking.dto.ConfirmBookingRequest;
import com.blackgoku.moviebooking.dto.HoldSeatsRequest;
import com.blackgoku.moviebooking.dto.HoldSeatsResponse;
import com.blackgoku.moviebooking.exception.PaymentFailedException;
import com.blackgoku.moviebooking.exception.SeatAlreadyBookedException;
import com.blackgoku.moviebooking.exception.SeatHoldExpiredException;
import com.blackgoku.moviebooking.exception.ShowNotFoundException;
import com.blackgoku.moviebooking.repository.BookingRepository;
import com.blackgoku.moviebooking.repository.ShowRepository;
import com.blackgoku.moviebooking.service.payment.PaymentGateway;
import com.blackgoku.moviebooking.service.payment.PaymentRequest;
import com.blackgoku.moviebooking.service.payment.PaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates the full booking flow:
 * <p>
 * POST /hold     -> Redis SETNX per seat, 10-min TTL, all-or-nothing across the batch
 * POST /confirm  -> idempotency check
 * -> Lua-script extend hold (atomic ownership check + TTL bump)
 * -> PaymentGateway.charge()  [Mock or Stripe via Spring profile]
 * -> @Transactional(timeout) DB write, DB unique constraint as final guard
 * -> on DB conflict: refund payment, surface SEAT_TAKEN
 * -> release Redis holds
 * <p>
 * Every step here traces back to a specific race condition worked through during
 * design review: the DB uniqueness constraint (double-booking), the Lua-script
 * extend (TTL-boundary race between hold expiry and a slow DB write), the
 * transaction timeout kept shorter than the Redis extension window (so the DB
 * gives up before the hold could expire mid-write), and the idempotency key check
 * up front (so a client retry after a timeout can't create a duplicate booking).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final SeatHoldService seatHoldService;
    private final ShowRepository showRepository;
    private final BookingRepository bookingRepository;
    private final PaymentGateway paymentGateway;
    private final BookingPersistenceService bookingPersistenceService;


    public HoldSeatsResponse holdSeats(HoldSeatsRequest request) {
        List<String> held = seatHoldService.tryHoldSeats(request.showId(), request.seatIds(), request.userId());

        if (held.isEmpty() && !request.seatIds().isEmpty()) {
            throw new SeatAlreadyBookedException(request.seatIds());
        }

        log.info("Held {} seats for user {} on show {}", held.size(), request.userId(), request.showId());
        return new HoldSeatsResponse(held, List.of(), Instant.now().plusSeconds(600));
    }

    /**
     * Idempotency check happens BEFORE any side effect - a retried request with the
     * same key returns the original result instead of re-running payment or booking.
     */
    public BookingResponse confirmBooking(ConfirmBookingRequest request) {
        Optional<Booking> existing = bookingRepository.findByIdempotencyKey(request.idempotencyKey());
        if (existing.isPresent()) {
            log.info("Idempotent replay for key {}, returning original booking", request.idempotencyKey());
            return toResponse(existing.get());
        }

        for (String seatId : request.seatIds()) {
            if (!seatHoldService.extendHoldIfOwner(request.showId(), seatId, request.userId())) {
                throw new SeatHoldExpiredException(seatId);
            }
        }

        BigDecimal totalAmount = calculateTotal(request.showId(), request.seatIds().size());

        PaymentResult paymentResult = paymentGateway.charge(
                PaymentRequest.builder()
                        .userId(request.userId())
                        .amount(totalAmount)
                        .currency("INR")
                        .idempotencyKey(request.idempotencyKey())
                        .build()
        );

        if (!paymentResult.success()) {
            request.seatIds().forEach(seatId ->
                    seatHoldService.releaseHold(request.showId(), seatId, request.userId()));
            throw new PaymentFailedException(paymentResult.failureReason());
        }

        try {
            Booking booking = bookingPersistenceService.persistBooking(
                    request, totalAmount, paymentResult.transactionReference());
            request.seatIds().forEach(seatId ->
                    seatHoldService.releaseHold(request.showId(), seatId, request.userId()));
            log.info("Booking {} confirmed for user {}", booking.getId(), request.userId());
            return toResponse(booking);

        } catch (DataIntegrityViolationException e) {
            log.warn("DB conflict on booking commit, refunding {}", paymentResult.transactionReference());
            paymentGateway.refund(paymentResult.transactionReference());
            throw new SeatAlreadyBookedException(request.seatIds());
        }
    }


    private BigDecimal calculateTotal(String showId, int seatCount) {
        Show show = showRepository.findById(showId).orElseThrow(() -> new ShowNotFoundException(showId));
        return show.getBasePrice().multiply(BigDecimal.valueOf(seatCount));
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .status(booking.getStatus().name())
                .paymentReference(booking.getPaymentReference())
                .amount(booking.getTotalAmount())
                .build();

    }
}
