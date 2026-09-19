package com.blackgoku.moviebooking.service;

import com.blackgoku.moviebooking.domain.BookingSeat;
import com.blackgoku.moviebooking.domain.BookingStatus;
import com.blackgoku.moviebooking.domain.Seat;
import com.blackgoku.moviebooking.domain.Show;
import com.blackgoku.moviebooking.dto.SeatMapEntry;
import com.blackgoku.moviebooking.exception.ShowNotFoundException;
import com.blackgoku.moviebooking.repository.BookingRepository;
import com.blackgoku.moviebooking.repository.SeatRepository;
import com.blackgoku.moviebooking.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Read-side of the show/seat-map. A seat's live status is DERIVED here, on read,
 * from two independent sources - Redis holds (fast, ephemeral) and confirmed
 * BookingSeat rows (durable) - rather than stored on the Seat entity itself. This
 * is what avoids a stale "HELD" status ever lingering after a Redis key expires:
 * there's simply no persisted status field that could go stale.
 */
@Service
@RequiredArgsConstructor
public class ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final SeatHoldService seatHoldService;

    public List<SeatMapEntry> getSeatMap(String showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException(showId));

        List<Seat> seats = seatRepository.findByScreenId(show.getScreen().getId());

        Set<String> bookedSeatIds = bookingRepository.findByShowId(showId).stream()
                .filter(b -> b.getStatus() == BookingStatus.CONFIRMED)
                .flatMap(b -> b.getSeats().stream())
                .map(BookingSeat::getSeatId)
                .collect(Collectors.toSet());

        return seats.stream()
                .map(seat -> SeatMapEntry.builder()
                        .seatId(seat.getId())
                        .seatNumber(seat.getSeatNumber())
                        .seatType(seat.getSeatType())
                        .price(show.getBasePrice())
                        .status(resolveStatus(showId, seat.getId(), bookedSeatIds))
                        .build()
                )
                .toList();
    }

    private String resolveStatus(String showId, String seatId, Set<String> bookedSeatIds) {
        if (bookedSeatIds.contains(seatId)) return "BOOKED";
        if (!seatHoldService.isFree(showId, seatId)) return "HELD";
        return "AVAILABLE";
    }
}
