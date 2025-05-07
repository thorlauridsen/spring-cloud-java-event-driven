package com.github.thorlauridsen.service;

import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.event.OrderCreatedEvent;
import com.github.thorlauridsen.model.repository.IOutboxEventRepo;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

/**
 * Service class for the order outbox.
 * This class is responsible for preparing events to be saved to the outbox table.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class OrderOutboxService {

    private final IOutboxEventRepo outboxRepo;

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
            log.warn("Could not prepare order with order status: {}", order.status());
            return;
        }
        val event = new OrderCreatedEvent(
                UUID.randomUUID(),
                order.id(),
                order.product(),
                order.amount()
        );
        outboxRepo.save(event);
    }
}
