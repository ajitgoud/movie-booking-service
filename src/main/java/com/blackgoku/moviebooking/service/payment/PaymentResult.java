package com.blackgoku.moviebooking.service.payment;

public record PaymentResult(boolean success, String transactionReference, String failureReason) {

    public static PaymentResult success(String transactionReference) {
        return new PaymentResult(true, transactionReference, null);
    }

    public static PaymentResult failure(String reason) {
        return new PaymentResult(false, null, reason);
    }
}
