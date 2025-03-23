package com.github.thorlauridsen.service;

import com.github.thorlauridsen.deduplication.DeduplicationService;
import com.github.thorlauridsen.model.enumeration.PaymentStatus;
import com.github.thorlauridsen.exception.PaymentNotFoundException;
import com.github.thorlauridsen.model.Payment;
import com.github.thorlauridsen.model.PaymentCreate;
import com.github.thorlauridsen.model.event.OrderCreatedEvent;
import com.github.thorlauridsen.persistence.IPaymentRepo;
import com.github.thorlauridsen.persistence.PaymentRepo;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Payment service class.
 * <p>
 * It is annotated with {@link Service} to allow Spring to automatically inject it where needed.
 * This class uses the {@link PaymentRepo} to interact with the repository.
 * <p>
 * The service class knows nothing about data transfer objects or database entities.
 * It only knows about the model classes and here you can implement business logic.
 * The idea here is to keep the various layers separated.
 */
@Service
public class PaymentService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DeduplicationService deduplicationService;
    private final PaymentOutboxService outboxService;
    private final IPaymentRepo paymentRepo;

    /**
     * Constructor for PaymentService.
     *
     * @param deduplicationService {@link DeduplicationService} for checking if an event has already been processed.
     * @param outboxService        {@link PaymentOutboxService} for preparing outbox events.
     * @param paymentRepo          {@link IPaymentRepo} for interacting with the payment table.
     */
    public PaymentService(
            DeduplicationService deduplicationService,
            PaymentOutboxService outboxService,
            IPaymentRepo paymentRepo
    ) {
        this.deduplicationService = deduplicationService;
        this.outboxService = outboxService;
        this.paymentRepo = paymentRepo;
    }

    /**
     * Process an order created event.
     * This method will create a new payment based on the event.
     * The payment will be saved to the database and an outbox event will be prepared.
     * For demonstration purposes, the payment status will be randomly set to either COMPLETED or FAILED.
     * <p>
     * This method is not idempotent as the payment status is randomly set.
     * This method will check if the event has already been processed by checking the deduplication service.
     * If the event has already been processed, it will log a warning and return.
     * If the event has not been processed, it will continue and record the event as processed.
     *
     * @param event {@link OrderCreatedEvent}.
     */
    public void processOrderCreated(OrderCreatedEvent event) {
        if (deduplicationService.isDuplicate(event.getId())) {
            logger.warn("Event already processed with id: {}", event.getId());
            return;
        }
        var random = new Random().nextBoolean();
        var status = PaymentStatus.COMPLETED;
        if (random) {
            status = PaymentStatus.FAILED;
        }
        var payment = new PaymentCreate(
                event.getOrderId(),
                status,
                event.getAmount()
        );
        var saved = paymentRepo.save(payment);
        deduplicationService.record(event.getId());
        outboxService.prepareEvent(saved);
    }

    /**
     * Find a payment by order id.
     *
     * @param orderId UUID of the order related to the payment.
     * @return {@link Payment}.
     * @throws PaymentNotFoundException if the payment is not found.
     */
    public Payment findByOrderId(UUID orderId) throws PaymentNotFoundException {
        logger.info("Finding payment with order id: {}", orderId);

        var payment = paymentRepo.findByOrderId(orderId);
        if (payment.isEmpty()) {
            throw new PaymentNotFoundException("Payment not found with order id: " + orderId);
        }
        logger.info("Found payment: {}", payment);
        return payment.get();
    }
}
