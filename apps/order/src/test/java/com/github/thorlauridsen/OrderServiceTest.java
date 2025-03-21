package com.github.thorlauridsen;

import com.github.thorlauridsen.deduplication.ProcessedEventRepo;
import com.github.thorlauridsen.enumeration.OrderStatus;
import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.event.PaymentFailedEvent;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.persistence.OrderRepo;
import com.github.thorlauridsen.service.OrderService;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private OutboxRepo outboxRepo;

    @Autowired
    private ProcessedEventRepo processedEventRepo;

    /**
     * Mocked SnsTemplate for testing.
     * Spring Cloud AWS SQS and SNS is disabled in the test profile.
     * So we need to mock this to ensure the producers still get a bean.
     */
    @MockitoBean
    private SnsTemplate snsTemplate;

    @BeforeEach
    public void setup() {
        orderRepo.deleteAll();
        outboxRepo.deleteAll();
        processedEventRepo.deleteAll();
        assertEquals(0, orderRepo.count());
        assertEquals(0, outboxRepo.count());
        assertEquals(0, processedEventRepo.count());
    }

    @Test
    public void createOrder_paymentCompleted() {
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
    public void createOrder_paymentFailed() {
        var created = createAndAssertOrder();
        var event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id()
        );
        orderService.processPaymentFailed(event);
        assertOrderStatus(created.id(), OrderStatus.CANCELLED);
    }

    @Test
    public void createOrder_paymentCompleted_deduplicationWorks() {
        var created = createAndAssertOrder();
        var event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id(),
                created.amount()
        );
        orderService.processPaymentCompleted(event);
        orderService.processPaymentCompleted(event);
        assertOrderStatus(created.id(), OrderStatus.COMPLETED);
    }

    @Test
    public void createOrder_paymentFailed_deduplicationWorks() {
        var created = createAndAssertOrder();
        var event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id()
        );
        orderService.processPaymentFailed(event);
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
     * This will also assert that the processed event is present in the database.
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

        assertEquals(1, processedEventRepo.count());
    }
}
