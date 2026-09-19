package com.blackgoku.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record HoldSeatsRequest(
        @NotBlank String showId,
        @NotEmpty List<String> seatIds,
        @NotBlank String userId
) {}
