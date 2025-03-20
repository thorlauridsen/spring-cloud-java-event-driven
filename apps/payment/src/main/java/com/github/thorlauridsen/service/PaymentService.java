package com.github.thorlauridsen.service;

import com.github.thorlauridsen.enumeration.PaymentStatus;
import com.github.thorlauridsen.event.OrderCreatedEvent;
import com.github.thorlauridsen.model.PaymentCreate;
import com.github.thorlauridsen.persistence.PaymentRepoFacade;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Payment service class.
 * <p>
 * It is annotated with {@link Service} to allow Spring to automatically inject it where needed.
 * This class uses the {@link PaymentRepoFacade} to interact with the repository.
 * <p>
 * The service class knows nothing about data transfer objects or database entities.
 * It only knows about the model classes and here you can implement business logic.
 * The idea here is to keep the various layers separated.
 */
@Service
public class PaymentService {

    private final PaymentOutboxService outboxService;
    private final PaymentRepoFacade paymentRepo;

    /**
     * Constructor for PaymentService.
     *
     * @param outboxService {@link PaymentOutboxService} for preparing outbox events.
     * @param paymentRepo   {@link PaymentRepoFacade} for interacting with the payment table.
     */
    public PaymentService(
            PaymentOutboxService outboxService,
            PaymentRepoFacade paymentRepo
    ) {
        this.outboxService = outboxService;
        this.paymentRepo = paymentRepo;
    }

    /**
     * Process an order created event.
     * This method will create a new payment based on the event.
     * The payment will be saved to the database and an outbox event will be prepared.
     * For demonstration purposes, the payment status will be randomly set to either COMPLETED or FAILED.
     *
     * @param event {@link OrderCreatedEvent}.
     */
    public void processOrderCreated(OrderCreatedEvent event) {
        var random = new Random().nextBoolean();
        var status = PaymentStatus.COMPLETED;
        if (random) {
            status = PaymentStatus.FAILED;
        }
        var payment = new PaymentCreate(
                event.getId(),
                status,
                event.getAmount()
        );
        var saved = paymentRepo.create(payment);
        outboxService.prepare(saved);
    }
}
