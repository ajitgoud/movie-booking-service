package com.blackgoku.moviebooking.repository;

import com.blackgoku.moviebooking.domain.Screen;
import com.blackgoku.moviebooking.domain.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository extends JpaRepository<Screen, String> {

    List<Screen> findByTheatreId(String theatreId);

}
