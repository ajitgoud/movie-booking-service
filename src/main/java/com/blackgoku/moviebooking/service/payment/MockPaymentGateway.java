package com.blackgoku.moviebooking.service.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@Profile({"mock", "default"})
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult charge(PaymentRequest request) {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("INVALID_AMOUNT");
        }

        if (ThreadLocalRandom.current().nextInt(100) < 5) {
            log.warn("Simulated card decline for user {}", request.userId());
            return PaymentResult.failure("CARD_DECLINED");
        }

        String txnRef = "MOCK-TXN-" + UUID.randomUUID();
        log.info("Charged {} {} for user {} -> {}", request.amount(), request.currency(), request.userId(), txnRef);
        return PaymentResult.success(txnRef);
    }

    @Override
    public void refund(String transactionReference) {
        log.info("Refunded mock transaction: {}", transactionReference);
    }
}
