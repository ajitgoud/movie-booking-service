package com.blackgoku.moviebooking.exception;

import java.util.List;

public class SeatAlreadyBookedException extends RuntimeException {
    public SeatAlreadyBookedException(List<String> seatIds) {
        super("Seats already taken: " + seatIds);
    }
}
