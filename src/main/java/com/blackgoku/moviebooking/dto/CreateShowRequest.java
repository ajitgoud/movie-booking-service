package com.blackgoku.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;

public record CreateShowRequest(
        @NotBlank String movieId,
        @NotBlank String screenId,
        @NotNull Instant startTime,
        @NotNull Instant endTime,
        @Positive BigDecimal basePrice
) {
}
