package com.github.thorlauridsen.service;

import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.event.PaymentFailedEvent;
import com.github.thorlauridsen.model.Payment;
import com.github.thorlauridsen.outbox.OutboxRepoFacade;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class for the payment outbox.
 * This class is responsible for preparing events to be saved to the outbox table.
 */
@Service
public class PaymentOutboxService {

    private final OutboxRepoFacade outboxRepo;

    /**
     * Constructor for PaymentOutboxService.
     *
     * @param outboxRepo {@link OutboxRepoFacade}
     */
    public PaymentOutboxService(OutboxRepoFacade outboxRepo) {
        this.outboxRepo = outboxRepo;
    }

    /**
     * Prepare an event to be saved to the outbox table.
     * This method will create a new event based on the payment status.
     * If the payment status is COMPLETED, a {@link PaymentCompletedEvent} will be created.
     * If the payment status is FAILED, a {@link PaymentFailedEvent} will be created.
     * The event will then be saved to the outbox table.
     *
     * @param payment {@link Payment}
     */
    public void prepare(Payment payment) {
        switch (payment.status()) {
            case COMPLETED -> {
                var event = new PaymentCompletedEvent(
                        UUID.randomUUID(),
                        payment.id(),
                        payment.orderId(),
                        payment.amount()
                );
                outboxRepo.save(event);
            }
            case FAILED -> {
                var event = new PaymentFailedEvent(
                        UUID.randomUUID(),
                        payment.id(),
                        payment.orderId()
                );
                outboxRepo.save(event);
            }
        }
    }
}
