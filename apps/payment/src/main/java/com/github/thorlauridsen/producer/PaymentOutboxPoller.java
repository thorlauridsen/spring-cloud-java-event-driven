package com.github.thorlauridsen.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.event.PaymentCompletedEventDto;
import com.github.thorlauridsen.event.PaymentFailedEventDto;
import com.github.thorlauridsen.model.event.OutboxEvent;
import com.github.thorlauridsen.outbox.BaseOutboxPoller;
import com.github.thorlauridsen.outbox.IOutboxEventRepo;
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
     * @param outboxEventRepo          {@link IOutboxEventRepo} for interacting with the outbox table.
     * @param paymentCompletedProducer {@link PaymentCompletedProducer} for publishing payment completed events.
     * @param paymentFailedProducer    {@link PaymentFailedProducer} for publishing payment failed events.
     */
    public PaymentOutboxPoller(
            ObjectMapper objectMapper,
            IOutboxEventRepo outboxEventRepo,
            PaymentCompletedProducer paymentCompletedProducer,
            PaymentFailedProducer paymentFailedProducer
    ) {
        super(objectMapper, outboxEventRepo);
        this.paymentCompletedProducer = paymentCompletedProducer;
        this.paymentFailedProducer = paymentFailedProducer;
    }

    /**
     * Process the event from the outbox table.
     * This will publish the event to the appropriate topic.
     *
     * @param event {@link OutboxEvent} to process.
     */
    @Override
    public void process(OutboxEvent event) {
        try {
            logger.info("Publishing payment event: {} - {}", event.eventType(), event.payload());

            switch (event.eventType()) {
                case PAYMENT_COMPLETED:
                    var completedEvent = objectMapper.readValue(event.payload(), PaymentCompletedEventDto.class);
                    paymentCompletedProducer.publish(completedEvent);
                    break;
                case PAYMENT_FAILED:
                    var failedEvent = objectMapper.readValue(event.payload(), PaymentFailedEventDto.class);
                    paymentFailedProducer.publish(failedEvent);
                    break;
                default:
                    logger.warn("Invalid payment event type: {}", event.eventType());
                    return;
            }
            outboxEventRepo.markAsProcessed(event.eventId());
            logger.info("Successfully processed payment outbox event: {} {}", event.eventType(), event.eventId());

        } catch (Exception e) {
            logger.error("Failed to process payment event: {}", event.eventId(), e);
        }
    }
}
