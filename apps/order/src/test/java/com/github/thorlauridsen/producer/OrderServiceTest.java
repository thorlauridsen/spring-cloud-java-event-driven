package com.github.thorlauridsen.producer;

import com.github.thorlauridsen.enumeration.OrderStatus;
import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.event.PaymentFailedEvent;
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
    }

    @Test
    public void createOrderPaymentCompleted() {
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

        var event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                created.id(),
                created.amount()
        );
        orderService.processPaymentCompleted(event);

        var optional = orderRepo.findById(created.id());
        assertTrue(optional.isPresent());

        var updated = optional.get().toModel();
        assertEquals("Computer", updated.product());
        assertEquals(199.0, updated.amount());
        assertEquals(OrderStatus.COMPLETED, updated.status());
    }

    @Test
    public void createOrderPaymentFailed() {
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

        var event = new PaymentFailedEvent(
                UUID.randomUUID(),
                created.id()
        );
        orderService.processPaymentFailed(event);

        var optional = orderRepo.findById(created.id());
        assertTrue(optional.isPresent());

        var updated = optional.get().toModel();
        assertEquals("Computer", updated.product());
        assertEquals(199.0, updated.amount());
        assertEquals(OrderStatus.CANCELLED, updated.status());
    }
}
