package com.github.thorlauridsen;

import com.github.thorlauridsen.enumeration.OrderStatus;
import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.event.PaymentFailedEvent;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.persistence.OrderRepo;
import com.github.thorlauridsen.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OutboxRepo outboxRepo;

    @BeforeEach
    public void setup() {
        orderRepo.deleteAll();
        outboxRepo.deleteAll();
        assertEquals(0, orderRepo.count());
        assertEquals(0, outboxRepo.count());
    }

    @Test
    public void createOrder_PaymentCompleted() {
        var created = createAndAssertOrder();
        var event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id(),
                created.amount()
        );
        orderService.processPaymentCompleted(event);
        assertOrderStatus(created.id(), OrderStatus.COMPLETED);
    }

    @Test
    public void createOrder_PaymentFailed() {
        var created = createAndAssertOrder();
        var event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id()
        );
        orderService.processPaymentFailed(event);
        assertOrderStatus(created.id(), OrderStatus.CANCELLED);
    }

    /**
     * Create an order and assert that it was created successfully.
     * This will assert that the order is present in the database.
     * It will also assert that the outbox event is present in the database.
     *
     * @return {@link Order} created order.
     */
    private Order createAndAssertOrder() {
        var order = new OrderCreate(
                "Computer",
                199.0
        );
        var created = orderService.create(order);

        assertNotNull(created);
        assertEquals("Computer", created.product());
        assertEquals(199.0, created.amount());

        assertEquals(1, orderRepo.count());
        assertEquals(1, outboxRepo.count());

        var orderEntity = orderRepo.findById(created.id());
        assertTrue(orderEntity.isPresent());

        return created;
    }

    /**
     * Assert that the order status is as expected.
     *
     * @param orderId        UUID of the order.
     * @param expectedStatus {@link OrderStatus} expected status of the order.
     */
    private void assertOrderStatus(UUID orderId, OrderStatus expectedStatus) {
        var optional = orderRepo.findById(orderId);
        assertTrue(optional.isPresent());

        var updated = optional.get().toModel();
        assertEquals("Computer", updated.product());
        assertEquals(199.0, updated.amount());
        assertEquals(expectedStatus, updated.status());
    }
}
