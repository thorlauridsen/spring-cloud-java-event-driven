package com.github.thorlauridsen.model.event;

import com.github.thorlauridsen.model.enumeration.EventType;
import java.util.UUID;

/**
 * This event represents when a payment has been completed.
 */
public class PaymentCompletedEvent extends BaseEvent {

    private final UUID paymentId;
    private final UUID orderId;
    private final double amount;

    /**
     * Constructor for PaymentCompletedEvent.
     *
     * @param id        UUID of the event.
     * @param paymentId UUID of the payment.
     * @param orderId   UUID of the order.
     * @param amount    amount of the payment.
     */
    public PaymentCompletedEvent(
            UUID id,
            UUID paymentId,
            UUID orderId,
            double amount
    ) {
        super(id, EventType.PAYMENT_COMPLETED);
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.amount = amount;
    }

    /**
     * Get the UUID of the order.
     *
     * @return UUID of the order.
     */
    public UUID getOrderId() {
        return orderId;
    }

    /**
     * Get the amount of the payment.
     *
     * @return amount of the payment.
     */
    public double getAmount() {
        return amount;
    }
}
