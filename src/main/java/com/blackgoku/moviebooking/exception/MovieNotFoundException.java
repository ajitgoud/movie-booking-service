package com.blackgoku.moviebooking.exception;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(String showId) {
        super("Movie not found: " + showId);
    }
}
