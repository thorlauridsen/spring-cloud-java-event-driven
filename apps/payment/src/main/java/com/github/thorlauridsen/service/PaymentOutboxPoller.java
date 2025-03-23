package com.github.thorlauridsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.event.PaymentFailedEvent;
import com.github.thorlauridsen.outbox.BaseOutboxPoller;
import com.github.thorlauridsen.outbox.OutboxEntity;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.producer.PaymentCompletedProducer;
import com.github.thorlauridsen.producer.PaymentFailedProducer;
import org.springframework.stereotype.Service;

/**
 * Poller for the outbox table.
 * This class will poll the outbox table for events and process them.
 * The purpose of this is to follow the transactional outbox pattern.
 * When state is updated, the event is saved to the outbox table.
 * The poller will then process the event and publish it to the appropriate topic.
 */
@Service
public class PaymentOutboxPoller extends BaseOutboxPoller {

    private final PaymentCompletedProducer paymentCompletedProducer;
    private final PaymentFailedProducer paymentFailedProducer;

    /**
     * Constructor for PaymentOutboxPoller.
     *
     * @param objectMapper             FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param outboxRepo               JpaRepository {@link OutboxRepo} for directly interacting with the outbox table.
     * @param paymentCompletedProducer {@link PaymentCompletedProducer} for publishing payment completed events.
     * @param paymentFailedProducer    {@link PaymentFailedProducer} for publishing payment failed events.
     */
    public PaymentOutboxPoller(
            ObjectMapper objectMapper,
            OutboxRepo outboxRepo,
            PaymentCompletedProducer paymentCompletedProducer,
            PaymentFailedProducer paymentFailedProducer
    ) {
        super(objectMapper, outboxRepo);
        this.paymentCompletedProducer = paymentCompletedProducer;
        this.paymentFailedProducer = paymentFailedProducer;
    }

    /**
     * Process the event from the outbox table.
     * This will publish the event to the appropriate topic.
     *
     * @param event {@link OutboxEntity} to process.
     */
    @Override
    public void process(OutboxEntity event) {
        try {
            logger.info("Publishing payment event: {} - {}", event.getEventType(), event.getPayload());

            switch (event.getEventType()) {
                case PAYMENT_COMPLETED:
                    var completedEvent = objectMapper.readValue(event.getPayload(), PaymentCompletedEvent.class);
                    paymentCompletedProducer.publish(completedEvent);
                    break;
                case PAYMENT_FAILED:
                    var failedEvent = objectMapper.readValue(event.getPayload(), PaymentFailedEvent.class);
                    paymentFailedProducer.publish(failedEvent);
                    break;
                default:
                    logger.warn("Invalid payment event type: {}", event.getEventType());
                    return;
            }
            var updated = OutboxEntity.markProcessed(event);
            outboxRepo.save(updated);
            logger.info("Successfully processed payment outbox event: {} {}", event.getEventType(), event.getEventId());

        } catch (Exception e) {
            logger.error("Failed to process payment event: {}", event.getEventId(), e);
        }
    }
}
