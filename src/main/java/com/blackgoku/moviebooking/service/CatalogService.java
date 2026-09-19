package com.blackgoku.moviebooking.service;


import com.blackgoku.moviebooking.domain.Movie;
import com.blackgoku.moviebooking.domain.Screen;
import com.blackgoku.moviebooking.domain.Show;
import com.blackgoku.moviebooking.domain.Theatre;
import com.blackgoku.moviebooking.dto.CreateMovieRequest;
import com.blackgoku.moviebooking.dto.CreateScreenRequest;
import com.blackgoku.moviebooking.dto.CreateShowRequest;
import com.blackgoku.moviebooking.dto.CreateTheatreRequest;
import com.blackgoku.moviebooking.exception.MovieNotFoundException;
import com.blackgoku.moviebooking.exception.ScreenNotFoundException;
import com.blackgoku.moviebooking.repository.MovieRepository;
import com.blackgoku.moviebooking.repository.ScreenRepository;
import com.blackgoku.moviebooking.repository.ShowRepository;
import com.blackgoku.moviebooking.repository.TheatreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Admin-side catalog management: creating movies, theatres, screens and shows.
 * Deliberately separate from BookingService - catalog writes and booking writes
 * have very different concurrency and consistency needs, and different reasons
 * to change, so they don't belong in the same class.
 */
@RequiredArgsConstructor
@Service
public class CatalogService {

    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final ShowRepository showRepository;

    public Movie createMovie(CreateMovieRequest request) {
        Movie movie = new Movie(UUID.randomUUID().toString(), request.title(),
                request.durationMinutes(), request.language(), request.genre());
        return movieRepository.save(movie);
    }

    public Theatre createTheatre(CreateTheatreRequest request) {
        Theatre theatre = new Theatre(UUID.randomUUID().toString(), request.name(),
                request.city(), request.address());
        return theatreRepository.save(theatre);
    }

    public Screen createScreen(CreateScreenRequest request) {
        Theatre theatre = theatreRepository.findById(request.theatreId())
                .orElseThrow(() -> new IllegalArgumentException("Theatre not found: " + request.theatreId()));
        Screen screen = new Screen(UUID.randomUUID().toString(), theatre,
                request.screenNumber(), request.screenType());
        return screenRepository.save(screen);
    }

    public Show createShow(CreateShowRequest request) {
        Movie movie = movieRepository.findById(request.movieId())
                .orElseThrow(() -> new MovieNotFoundException(request.movieId()));
        Screen screen = screenRepository.findById(request.screenId())
                .orElseThrow(() -> new ScreenNotFoundException(request.screenId()));

        Show show = new Show(UUID.randomUUID().toString(), movie, screen,
                request.startTime(), request.endTime(), request.basePrice());
        return showRepository.save(show);
    }
}
