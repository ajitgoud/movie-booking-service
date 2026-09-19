package com.blackgoku.moviebooking.exception;

public class SeatHoldExpiredException extends RuntimeException {
    public SeatHoldExpiredException(String seatId) {
        super("Hold expired for seat " + seatId + ". Please select your seats again.");
    }
}
