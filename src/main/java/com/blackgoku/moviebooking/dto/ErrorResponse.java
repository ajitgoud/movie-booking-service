package com.blackgoku.moviebooking.dto;

import lombok.Builder;

@Builder
public record ErrorResponse(String code, String message) {}
