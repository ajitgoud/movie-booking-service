package com.blackgoku.moviebooking.controller;

import com.blackgoku.moviebooking.domain.Movie;
import com.blackgoku.moviebooking.dto.CreateMovieRequest;
import com.blackgoku.moviebooking.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/movies")
public class MovieController {

    private final CatalogService catalogService;

    @PostMapping
    public ResponseEntity<Movie> create(@Valid @RequestBody CreateMovieRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createMovie(request));
    }
}