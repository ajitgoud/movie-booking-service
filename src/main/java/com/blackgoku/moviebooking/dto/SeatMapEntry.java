package com.blackgoku.moviebooking.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SeatMapEntry(
        String seatId,
        String seatNumber,
        String seatType,
        BigDecimal price,
        String status // AVAILABLE, HELD, BOOKED
) {}
