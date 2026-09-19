package com.blackgoku.moviebooking.repository;

import com.blackgoku.moviebooking.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, String> {

}
