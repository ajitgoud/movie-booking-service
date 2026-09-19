package com.blackgoku.moviebooking.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record HoldSeatsResponse(
        List<String> heldSeatIds,
        List<String> rejectedSeatIds,
        Instant holdExpiresAt
) {}
