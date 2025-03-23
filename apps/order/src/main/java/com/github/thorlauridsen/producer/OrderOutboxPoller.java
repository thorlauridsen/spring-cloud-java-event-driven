package com.github.thorlauridsen.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.model.enumeration.EventType;
import com.github.thorlauridsen.event.OrderCreatedEventDto;
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
public class OrderOutboxPoller extends BaseOutboxPoller {

    private final OrderCreatedProducer orderCreatedProducer;

    /**
     * Constructor for OrderOutboxPoller.
     *
     * @param objectMapper         FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param orderCreatedProducer {@link OrderCreatedProducer} to publish the order created event.
     * @param outboxEventRepo      {@link IOutboxEventRepo} for interacting with the outbox table.
     */
    public OrderOutboxPoller(
            ObjectMapper objectMapper,
            OrderCreatedProducer orderCreatedProducer,
            IOutboxEventRepo outboxEventRepo
    ) {
        super(objectMapper, outboxEventRepo);
        this.orderCreatedProducer = orderCreatedProducer;
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
            logger.info("Publishing order event: {} - {}", event.eventType(), event.payload());

            if (event.eventType() != EventType.ORDER_CREATED) {
                logger.warn("Invalid order event type: {}", event.eventType());
                return;
            }
            var createdEvent = objectMapper.readValue(event.payload(), OrderCreatedEventDto.class);
            orderCreatedProducer.publish(createdEvent);

            outboxEventRepo.markAsProcessed(event.eventId());
            logger.info("Successfully processed order outbox event: {} {}", event.eventType(), event.eventId());

        } catch (Exception e) {
            logger.error("Failed to process order event: {}", event.eventId(), e);
        }
    }
}
