package com.github.thorlauridsen.service;

import com.github.thorlauridsen.deduplication.DeduplicationService;
import com.github.thorlauridsen.exception.OrderNotFoundException;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.model.event.PaymentCompletedEvent;
import com.github.thorlauridsen.model.event.PaymentFailedEvent;
import com.github.thorlauridsen.model.repository.IOrderRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Order service class.
 * <p>
 * It is annotated with {@link Service} to allow Spring to automatically inject it where needed.
 * This class uses the {@link IOrderRepo} to interact with the repository.
 * <p>
 * The service class knows nothing about data transfer objects or database entities.
 * It only knows about the model classes and here you can implement business logic.
 * The idea here is to keep the various layers separated.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class OrderService {

    private final DeduplicationService deduplicationService;
    private final OrderOutboxService outboxService;
    private final IOrderRepo orderRepo;

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
            log.warn("Event already processed with id: {}", event.getId());
            return;
        }
        updateOrder(event.getOrderId(), OrderStatus.COMPLETED);
        deduplicationService.recordEvent(event.getId());
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
            log.warn("Event already processed with id: {}", event.getId());
            return;
        }
        updateOrder(event.getOrderId(), OrderStatus.CANCELLED);
        deduplicationService.recordEvent(event.getId());
    }

    /**
     * Create a new order.
     *
     * @param order {@link OrderCreate} for creating a new order.
     * @return {@link Order}.
     */
    public Order create(OrderCreate order) {
        log.info("Creating order: {}", order);

        val saved = orderRepo.create(order);
        log.info("Order created with id: {}", saved.id());

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
        log.info("Finding order with id: {}", id);

        val order = orderRepo.findById(id);
        if (order.isEmpty()) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }
        log.info("Found order: {}", order);
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

        val optionalOrder = orderRepo.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new OrderNotFoundException("Order not found with id: " + id);
        }

        val foundOrder = optionalOrder.get();
        val order = Order.updateStatus(foundOrder, status);
        val updated = orderRepo.update(order);

        log.info("Set order status to {} for order id {}", status, updated.id());
    }
}
