package com.blackgoku.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateScreenRequest(
        @NotBlank String theatreId,
        @NotBlank String screenNumber,
        @NotBlank String screenType
) {
}
