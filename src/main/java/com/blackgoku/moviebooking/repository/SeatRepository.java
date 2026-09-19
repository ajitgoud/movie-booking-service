package com.blackgoku.moviebooking.repository;

import com.blackgoku.moviebooking.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, String> {
    List<Seat> findByScreenId(String screenId);
}
