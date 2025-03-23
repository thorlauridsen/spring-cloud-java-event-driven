package com.github.thorlauridsen.service;

import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.event.OrderCreatedEvent;
import com.github.thorlauridsen.outbox.IOutboxEventRepo;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service class for the order outbox.
 * This class is responsible for preparing events to be saved to the outbox table.
 */
@Service
public class OrderOutboxService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final IOutboxEventRepo outboxRepo;

    /**
     * Constructor for OrderOutboxService.
     *
     * @param outboxRepo {@link IOutboxEventRepo} for interacting with the outbox table.
     */
    public OrderOutboxService(IOutboxEventRepo outboxRepo) {
        this.outboxRepo = outboxRepo;
    }

    /**
     * Prepare an event to be saved to the outbox table.
     * This method will create a new event based on the order status.
     * If the order status is CREATED, a {@link OrderCreatedEvent} will be created.
     * The event will then be saved to the outbox table.
     *
     * @param order {@link Order}
     */
    public void prepareEvent(Order order) {
        if (order.status() != OrderStatus.CREATED) {
            logger.warn("Could not prepare order with order status: {}", order.status());
            return;
        }
        var event = new OrderCreatedEvent(
                UUID.randomUUID(),
                order.id(),
                order.product(),
                order.amount()
        );
        outboxRepo.save(event);
    }
}
