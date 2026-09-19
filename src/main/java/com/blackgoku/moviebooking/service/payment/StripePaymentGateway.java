package com.blackgoku.moviebooking.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Profile("stripe")
public class StripePaymentGateway implements PaymentGateway {

    public StripePaymentGateway(@Value("${payment.stripe.api-key}") String apiKey) {
        Stripe.apiKey = apiKey;
    }

    @Override
    public PaymentResult charge(PaymentRequest request) {
        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(toSmallestCurrencyUnit(request.amount()))
                    .setCurrency(request.currency().toLowerCase())
                    .putMetadata("idempotency_key", request.idempotencyKey())
                    .putMetadata("user_id", request.userId())
                    .build();

            RequestOptions options = RequestOptions.builder()
                    .setIdempotencyKey(request.idempotencyKey())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params, options);
            return PaymentResult.success(intent.getId());
        } catch (StripeException e) {
            log.error("Stripe charge failed", e);
            return PaymentResult.failure(e.getMessage());
        }
    }

    @Override
    public void refund(String transactionReference) {
        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(transactionReference)
                    .build();
            Refund.create(params);
        } catch (StripeException e) {
            throw new RuntimeException("Refund failed for " + transactionReference, e);
        }
    }

    private long toSmallestCurrencyUnit(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100)).longValue();
    }
}