package com.blackgoku.moviebooking.repository;

import com.blackgoku.moviebooking.domain.Show;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowRepository extends JpaRepository<Show, String> {
}
