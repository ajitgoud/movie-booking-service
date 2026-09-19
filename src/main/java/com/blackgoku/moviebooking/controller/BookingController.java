package com.blackgoku.moviebooking.controller;

import com.blackgoku.moviebooking.dto.BookingResponse;
import com.blackgoku.moviebooking.dto.ConfirmBookingRequest;
import com.blackgoku.moviebooking.dto.HoldSeatsRequest;
import com.blackgoku.moviebooking.dto.HoldSeatsResponse;
import com.blackgoku.moviebooking.service.BookingService;
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
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping("/hold")
    public ResponseEntity<HoldSeatsResponse> holdSeats(@Valid @RequestBody HoldSeatsRequest request) {
        return ResponseEntity.ok(bookingService.holdSeats(request));
    }

    @PostMapping("/confirm")
    public ResponseEntity<BookingResponse> confirm(@Valid @RequestBody ConfirmBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.confirmBooking(request));
    }
}
