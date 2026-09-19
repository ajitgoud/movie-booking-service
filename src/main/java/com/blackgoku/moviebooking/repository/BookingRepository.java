package com.blackgoku.moviebooking.repository;

import com.blackgoku.moviebooking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, String> {

    Optional<Booking> findByIdempotencyKey(String idempotencyKey);

    List<Booking> findByShowId(String showId);
}
