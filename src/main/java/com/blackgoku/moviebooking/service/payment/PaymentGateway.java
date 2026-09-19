package com.blackgoku.moviebooking.service.payment;

/**
 * Adapter pattern: BookingService depends only on this interface, never on a
 * specific provider. Swapping Mock -> Stripe -> Paytm is a Spring profile change,
 * not a code change in the booking flow.
 */
public interface PaymentGateway {
    PaymentResult charge(PaymentRequest request);
    void refund(String transactionReference);
}
