package com.github.thorlauridsen.model;

import com.github.thorlauridsen.model.enumeration.PaymentStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Record class representing the domain model "Payment".
 *
 * @param id      UUID of the payment.
 * @param orderId UUID of the related order.
 * @param time    time payment was created in database.
 * @param status  current payment status.
 * @param amount  amount to be paid (or that has been paid if complete)
 */
public record Payment(
        UUID id,
        UUID orderId,
        OffsetDateTime time,
        PaymentStatus status,
        double amount
) {
}
