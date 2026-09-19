package com.blackgoku.moviebooking.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BookingResponse(
        String bookingId,
        String status,
        String paymentReference,
        BigDecimal amount
) {}
