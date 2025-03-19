package com.github.thorlauridsen.service;

import com.github.thorlauridsen.enumeration.OrderStatus;
import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.event.PaymentFailedEvent;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import com.github.thorlauridsen.persistence.OrderRepoFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Order service class.
 * <p>
 * It is annotated with {@link Service} to allow Spring to automatically inject it where needed.
 * This class uses the {@link OrderRepoFacade} to interact with the repository.
 * <p>
 * The service class knows nothing about data transfer objects or database entities.
 * It only knows about the model classes and here you can implement business logic.
 * The idea here is to keep the various layers separated.
 */
@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OrderRepoFacade orderRepo;
    private final OrderOutboxService outboxService;

    /**
     * Constructor for OrderService.
     *
     * @param orderRepo     {@link OrderRepoFacade} for interacting with the order repository.
     * @param outboxService {@link OrderOutboxService} for preparing outbox events.
     */
    public OrderService(
            OrderRepoFacade orderRepo,
            OrderOutboxService outboxService
    ) {
        this.orderRepo = orderRepo;
        this.outboxService = outboxService;
    }

    /**
     * Process a payment completed event.
     * If payment is completed, the order status will be set to COMPLETED.
     *
     * @param event {@link PaymentCompletedEvent}.
     */
    public void processPaymentCompleted(PaymentCompletedEvent event) {
        logger.info("Received PaymentCompletedEvent: {}", event);
        updateOrder(event.getOrderId(), OrderStatus.COMPLETED);
    }

    /**
     * Process a payment failed event.
     * If payment is failed, the order status will be set to CANCELLED.
     *
     * @param event {@link PaymentFailedEvent}.
     */
    public void processPaymentFailed(PaymentFailedEvent event) {
        logger.info("Received PaymentFailedEvent: {}", event);
        updateOrder(event.getOrderId(), OrderStatus.CANCELLED);
    }

    /**
     * Create a new order.
     *
     * @param order {@link OrderCreate} for creating a new order.
     * @return {@link Order}.
     */
    public Order create(OrderCreate order) {
        logger.info("Creating order: {}", order);
        var saved = orderRepo.create(order);
        logger.info("Order created with id: {}", saved.id());

        outboxService.prepare(saved);
        return saved;
    }

    /**
     * Update an existing order.
     *
     * @param id     UUID of the order.
     * @param status {@link OrderStatus} to update the order with.
     */
    public void updateOrder(
            UUID id,
            OrderStatus status
    ) {
        var optionalOrder = orderRepo.findById(id);
        if (optionalOrder.isEmpty()) {
            logger.warn("Order not found with id: {}", id);
            return;
        }

        var foundOrder = optionalOrder.get();
        var order = Order.updateStatus(foundOrder, status);
        var updated = orderRepo.update(order);

        logger.info("Set order status to {} for order id {}", status, updated.id());
    }
}
