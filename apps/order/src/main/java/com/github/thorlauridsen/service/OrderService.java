package com.github.thorlauridsen.service;

import com.github.thorlauridsen.deduplication.DeduplicationService;
import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.exception.OrderNotFoundException;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import com.github.thorlauridsen.model.event.PaymentCompletedEvent;
import com.github.thorlauridsen.model.event.PaymentFailedEvent;
import com.github.thorlauridsen.model.repository.IOrderRepo;
import com.github.thorlauridsen.persistence.OrderRepo;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Order service class.
 * <p>
 * It is annotated with {@link Service} to allow Spring to automatically inject it where needed.
 * This class uses the {@link OrderRepo} to interact with the repository.
 * <p>
 * The service class knows nothing about data transfer objects or database entities.
 * It only knows about the model classes and here you can implement business logic.
 * The idea here is to keep the various layers separated.
 */
@Service
public class OrderService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DeduplicationService deduplicationService;
    private final OrderOutboxService outboxService;
    private final IOrderRepo orderRepo;

    /**
     * Constructor for OrderService.
     *
     * @param deduplicationService {@link DeduplicationService} for checking if an event has already been processed.
     * @param orderRepo            {@link IOrderRepo} for interacting with the order repository.
     * @param outboxService        {@link OrderOutboxService} for preparing outbox events.
     */
    public OrderService(
            DeduplicationService deduplicationService,
            OrderOutboxService outboxService,
            IOrderRepo orderRepo
    ) {
        this.deduplicationService = deduplicationService;
        this.outboxService = outboxService;
        this.orderRepo = orderRepo;
    }

    /**
     * Process a payment completed event.
     * If payment is completed, the order status will be set to COMPLETED.
     * <p>
     * This method will also check if the event has already been processed by checking the deduplication service.
     * If the event has already been processed, it will log a warning and return.
     * If the event has not been processed, it will continue and record the event as processed.
     * This might be redundant as this method is already idempotent but this is just for showcasing.
     *
     * @param event {@link PaymentCompletedEvent}.
     * @throws OrderNotFoundException if the order is not found.
     */
    public void processPaymentCompleted(PaymentCompletedEvent event) throws OrderNotFoundException {
        if (deduplicationService.isDuplicate(event.getId())) {
            logger.warn("Event already processed with id: {}", event.getId());
            return;
        }
        updateOrder(event.getOrderId(), OrderStatus.COMPLETED);
        deduplicationService.record(event.getId());
    }

    /**
     * Process a payment failed event.
     * If payment is failed, the order status will be set to CANCELLED.
     * <p>
     * This method will also check if the event has already been processed by checking the deduplication service.
     * If the event has already been processed, it will log a warning and return.
     * If the event has not been processed, it will continue and record the event as processed.
     * This might be redundant as this method is already idempotent but this is just for showcasing.
     *
     * @param event {@link PaymentFailedEvent}.
     * @throws OrderNotFoundException if the order is not found.
     */
    public void processPaymentFailed(PaymentFailedEvent event) throws OrderNotFoundException {
        if (deduplicationService.isDuplicate(event.getId())) {
            logger.warn("Event already processed with id: {}", event.getId());
            return;
        }
        updateOrder(event.getOrderId(), OrderStatus.CANCELLED);
        deduplicationService.record(event.getId());
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

        outboxService.prepareEvent(saved);
        return saved;
    }

    /**
     * Find an order by id.
     *
     * @param id UUID of the order.
     * @return {@link Order}.
     * @throws OrderNotFoundException if the order is not found.
     */
    public Order findById(UUID id) throws OrderNotFoundException {
        logger.info("Finding order with id: {}", id);

        var order = orderRepo.findById(id);
        if (order.isEmpty()) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        logger.info("Found order: {}", order);
        return order.get();
    }

    /**
     * Update an existing order.
     *
     * @param id     UUID of the order.
     * @param status {@link OrderStatus} to update the order with.
     * @throws OrderNotFoundException if the order is not found.
     */
    private void updateOrder(
            UUID id,
            OrderStatus status
    ) throws OrderNotFoundException {

        var optionalOrder = orderRepo.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }

        var foundOrder = optionalOrder.get();
        var order = Order.updateStatus(foundOrder, status);
        var updated = orderRepo.update(order);

        logger.info("Set order status to {} for order id {}", status, updated.id());
    }
}
