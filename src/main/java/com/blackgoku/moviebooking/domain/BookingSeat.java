package com.blackgoku.moviebooking.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * The UNIQUE(show_id, seat_id) constraint is the real correctness guarantee
 * against double-booking - independent of whatever the Redis hold layer does
 * or doesn't catch under a timing race.
 */
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "booking_seats", uniqueConstraints = @UniqueConstraint(columnNames = {"show_id", "seat_id"}))
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    @Column(name = "show_id")
    private String showId;

    @Column(name = "seat_id")
    private String seatId;
}
