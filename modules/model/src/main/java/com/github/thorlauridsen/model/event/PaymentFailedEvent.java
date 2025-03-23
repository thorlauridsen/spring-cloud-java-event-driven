package com.github.thorlauridsen.model.event;

import com.github.thorlauridsen.model.enumeration.EventType;
import java.util.UUID;

/**
 * This event represents when a payment has failed.
 */
public class PaymentFailedEvent extends BaseEvent {

    private final UUID paymentId;
    private final UUID orderId;

    /**
     * Constructor for PaymentFailedEvent.
     *
     * @param id        UUID of the event.
     * @param paymentId UUID of the payment.
     * @param orderId   UUID of the order.
     */
    public PaymentFailedEvent(
            UUID id,
            UUID paymentId,
            UUID orderId
    ) {
        super(id, EventType.PAYMENT_FAILED);
        this.paymentId = paymentId;
        this.orderId = orderId;
    }

    /**
     * Get the UUID of the order.
     *
     * @return UUID of the order.
     */
    public UUID getOrderId() {
        return orderId;
    }
}
