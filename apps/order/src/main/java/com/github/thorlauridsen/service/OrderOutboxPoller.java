package com.github.thorlauridsen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.event.EventType;
import com.github.thorlauridsen.event.OrderCreatedEvent;
import com.github.thorlauridsen.outbox.BaseOutboxPoller;
import com.github.thorlauridsen.outbox.OutboxEntity;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.producer.OrderCreatedProducer;
import org.springframework.stereotype.Service;

/**
 * Poller for the outbox table.
 * This class will poll the outbox table for events and process them.
 * The purpose of this is to follow the transactional outbox pattern.
 * When state is updated, the event is saved to the outbox table.
 * The poller will then process the event and publish it to the appropriate topic.
 */
@Service
public class OrderOutboxPoller extends BaseOutboxPoller {

    private final OrderCreatedProducer orderCreatedProducer;

    /**
     * Constructor for OrderOutboxPoller.
     *
     * @param objectMapper         FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param orderCreatedProducer {@link OrderCreatedProducer} to publish the order created event.
     * @param outboxRepo           JpaRepository {@link OutboxRepo} for directly interacting with the outbox table.
     */
    public OrderOutboxPoller(
            ObjectMapper objectMapper,
            OrderCreatedProducer orderCreatedProducer,
            OutboxRepo outboxRepo
    ) {
        super(objectMapper, outboxRepo);
        this.orderCreatedProducer = orderCreatedProducer;
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
            logger.info("Publishing order event: {} - {}", event.getEventType(), event.getPayload());

            if (event.getEventType() != EventType.ORDER_CREATED) {
                logger.warn("Invalid order event type: {}", event.getEventType());
                return;
            }
            var createdEvent = objectMapper.readValue(event.getPayload(), OrderCreatedEvent.class);
            orderCreatedProducer.publish(createdEvent);

            var updated = OutboxEntity.markProcessed(event);
            outboxRepo.save(updated);
            logger.info("Successfully processed order outbox event: {} {}", event.getEventType(), event.getEventId());

        } catch (Exception e) {
            logger.error("Failed to process order event: {}", event.getEventId(), e);
        }
    }
}
