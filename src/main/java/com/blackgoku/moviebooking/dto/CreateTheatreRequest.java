package com.blackgoku.moviebooking.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTheatreRequest(
        @NotBlank String name,
        @NotBlank String city,
        @NotBlank String address
) {
}
