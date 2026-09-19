package com.blackgoku.moviebooking.service.payment;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record PaymentRequest(String userId, BigDecimal amount, String currency, String idempotencyKey) {}
