package com.blackgoku.moviebooking.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Belongs to a SCREEN, not a Show. A screen's physical layout (A1, A2, B1...)
 * is fixed and created ONCE - it is not duplicated per show. What varies per
 * show is availability, which is resolved separately (see ShowService) by
 * checking Redis holds + confirmed BookingSeat rows for a given (showId, seatId)
 * pair. This was the normalization bug in the original draft of this project:
 * Seat rows were originally created per-show, which would have meant re-creating
 * the same physical layout for every new showing on the same screen.
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "seats", uniqueConstraints = @UniqueConstraint(columnNames = {"screen_id", "seat_number"}))
public class Seat {

    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;

    private String seatNumber; // "A1"
    private String seatType;   // REGULAR, PREMIUM

}
