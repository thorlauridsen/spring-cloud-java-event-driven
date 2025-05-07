package com.github.thorlauridsen.service;

import com.github.thorlauridsen.model.Payment;
import com.github.thorlauridsen.model.event.PaymentCompletedEvent;
import com.github.thorlauridsen.model.event.PaymentFailedEvent;
import com.github.thorlauridsen.outbox.OutboxEventRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * Service class for the payment outbox.
 * This class is responsible for preparing events to be saved to the outbox table.
 */
@RequiredArgsConstructor
@Service
public class PaymentOutboxService {

    private final OutboxEventRepo outboxRepo;

    /**
     * Prepare an event to be saved to the outbox table.
     * This method will create a new event based on the payment status.
     * If the payment status is COMPLETED, a {@link PaymentCompletedEvent} will be created.
     * If the payment status is FAILED, a {@link PaymentFailedEvent} will be created.
     * The event will then be saved to the outbox table.
     *
     * @param payment {@link Payment}
     */
    public void prepareEvent(Payment payment) {
        switch (payment.status()) {
            case COMPLETED -> {
                val event = new PaymentCompletedEvent(
                        UUID.randomUUID(),
                        payment.id(),
                        payment.orderId(),
                        payment.amount()
                );
                outboxRepo.save(event);
            }
            case FAILED -> {
                val event = new PaymentFailedEvent(
                        UUID.randomUUID(),
                        payment.id(),
                        payment.orderId()
                );
                outboxRepo.save(event);
            }
        }
    }
}
