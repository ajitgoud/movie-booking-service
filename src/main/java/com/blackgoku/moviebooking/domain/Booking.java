package com.blackgoku.moviebooking.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private String id;

    @Column(name = "show_id")
    private String showId;

    private String userId;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING_PAYMENT;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String paymentReference;
    private BigDecimal totalAmount;

    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "idempotency_key", unique = true)
    private String idempotencyKey;

    @Builder.Default
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingSeat> seats = new ArrayList<>();

    public void addSeat(BookingSeat seat) {
        seats.add(seat);
        seat.setBooking(this);
    }

}