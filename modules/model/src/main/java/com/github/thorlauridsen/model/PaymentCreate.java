package com.github.thorlauridsen.model;

import com.github.thorlauridsen.enumeration.PaymentStatus;
import java.util.UUID;

/**
 * Record class representing the fields necessary for creating a payment.
 *
 * @param orderId UUID of the related order.
 * @param status  current payment status.
 * @param amount  amount to be paid (or that has been paid if complete)
 */
public record PaymentCreate(
        UUID orderId,
        PaymentStatus status,
        double amount
) {
}
