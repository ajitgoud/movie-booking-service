package com.blackgoku.moviebooking.exception;

public class ScreenNotFoundException extends RuntimeException {
    public ScreenNotFoundException(String showId) {
        super("Screen not found: " + showId);
    }
}
