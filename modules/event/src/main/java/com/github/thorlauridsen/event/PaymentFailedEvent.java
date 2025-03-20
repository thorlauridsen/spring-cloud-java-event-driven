package com.github.thorlauridsen.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * This event represents when a payment has failed.
 */
public class PaymentFailedEvent extends BaseEvent {

    private final UUID paymentId;
    private final UUID orderId;

    @JsonCreator
    public PaymentFailedEvent(
            @JsonProperty("id") UUID id,
            @JsonProperty("paymentId") UUID paymentId,
            @JsonProperty("orderId") UUID orderId
    ) {
        super(id, EventType.PAYMENT_FAILED);
        this.paymentId = paymentId;
        this.orderId = orderId;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
