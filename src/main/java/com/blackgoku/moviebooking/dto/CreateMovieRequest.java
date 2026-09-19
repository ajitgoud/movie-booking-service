package com.blackgoku.moviebooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateMovieRequest(
        @NotBlank String title,
        @Min(1) int durationMinutes,
        @NotBlank String language,
        @NotBlank String genre
) {}
