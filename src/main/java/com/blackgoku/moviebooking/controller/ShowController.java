package com.blackgoku.moviebooking.controller;


import com.blackgoku.moviebooking.domain.Show;
import com.blackgoku.moviebooking.dto.CreateShowRequest;
import com.blackgoku.moviebooking.dto.SeatMapEntry;
import com.blackgoku.moviebooking.service.CatalogService;
import com.blackgoku.moviebooking.service.ShowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/shows")
public class ShowController {

    private final ShowService showService;
    private final CatalogService catalogService;

    @PostMapping
    public ResponseEntity<Show> create(@Valid @RequestBody CreateShowRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(catalogService.createShow(request));
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<List<SeatMapEntry>> getSeatMap(@PathVariable("showId") String showId) {
        return ResponseEntity.ok(showService.getSeatMap(showId));
    }
}
