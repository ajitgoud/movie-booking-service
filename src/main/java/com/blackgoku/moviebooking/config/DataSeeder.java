package com.blackgoku.moviebooking.config;

import com.blackgoku.moviebooking.domain.Movie;
import com.blackgoku.moviebooking.domain.Screen;
import com.blackgoku.moviebooking.domain.Seat;
import com.blackgoku.moviebooking.domain.Show;
import com.blackgoku.moviebooking.domain.Theatre;
import com.blackgoku.moviebooking.repository.MovieRepository;
import com.blackgoku.moviebooking.repository.ScreenRepository;
import com.blackgoku.moviebooking.repository.SeatRepository;
import com.blackgoku.moviebooking.repository.ShowRepository;
import com.blackgoku.moviebooking.repository.TheatreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Seeds a full catalog on startup - Theatre -> Screen -> Seat (created ONCE per
 * screen), Movie, and one Show tying a movie to a screen at a specific time -
 * so the API is immediately testable without hand-writing SQL first.
 */
@Slf4j
@Component
public class DataSeeder implements CommandLineRunner {

    public static final String DEMO_SHOW_ID = "show-1";

    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    public DataSeeder(TheatreRepository theatreRepository, ScreenRepository screenRepository,
                      SeatRepository seatRepository, MovieRepository movieRepository,
                      ShowRepository showRepository) {
        this.theatreRepository = theatreRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
    }

    @Override
    public void run(String... args) {
        if (showRepository.existsById(DEMO_SHOW_ID)) {
            log.info("Demo data already seeded, skipping.");
            return;
        }

        Theatre theatre = new Theatre("theatre-1", "PVR Forum Mall", "Bengaluru", "Koramangala");
        theatreRepository.save(theatre);

        Screen screen = new Screen("screen-1", theatre, "Screen 1", "IMAX");
        screenRepository.save(screen);

        char[] rows = {'A', 'B'};
        for (char row : rows) {
            String seatType = (row == 'A') ? "PREMIUM" : "REGULAR";
            for (int i = 1; i <= 5; i++) {
                String seatNumber = row + String.valueOf(i);
                Seat seat = new Seat("seat-" + seatNumber, screen, seatNumber, seatType);
                seatRepository.save(seat);
            }
        }

        Movie movie = new Movie("movie-1", "Inception", 148, "English", "Sci-Fi");
        movieRepository.save(movie);

        Instant startTime = Instant.now().plus(2, ChronoUnit.HOURS);
        Show show = new Show(DEMO_SHOW_ID, movie, screen, startTime,
                startTime.plusSeconds(movie.getDurationMinutes() * 60L), BigDecimal.valueOf(300));
        showRepository.save(show);

        log.info("Seeded demo catalog: theatre={}, screen={}, movie={}, show={}, seats=10",
                theatre.getId(), screen.getId(), movie.getId(), show.getId());
    }
}
