package com.github.thorlauridsen;

import com.github.thorlauridsen.deduplication.ProcessedEventJpaRepo;
import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.exception.OrderNotFoundException;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.OrderCreate;
import com.github.thorlauridsen.model.event.PaymentCompletedEvent;
import com.github.thorlauridsen.model.event.PaymentFailedEvent;
import com.github.thorlauridsen.outbox.OutboxEventJpaRepo;
import com.github.thorlauridsen.persistence.OrderJpaRepo;
import com.github.thorlauridsen.service.OrderService;
import io.awspring.cloud.sns.core.SnsTemplate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderJpaRepo orderRepo;

    @Autowired
    private OutboxEventJpaRepo outboxEventRepo;

    @Autowired
    private ProcessedEventJpaRepo processedEventRepo;

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
        outboxEventRepo.deleteAll();
        processedEventRepo.deleteAll();
        assertEquals(0, outboxEventRepo.count());
        assertEquals(0, orderRepo.count());
        assertEquals(0, processedEventRepo.count());
    }

    @Test
    public void getOrder_noOrderExists() {
        assertThrows(OrderNotFoundException.class, () -> orderService.findById(UUID.randomUUID()));
    }

    @Test
    public void processPaymentCompleted_noOrderExists() {
        var event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                199.0
        );
        assertThrows(OrderNotFoundException.class, () -> orderService.processPaymentCompleted(event));
    }

    @Test
    public void processPaymentFailed_noOrderExists() {
        var event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        assertThrows(OrderNotFoundException.class, () -> orderService.processPaymentFailed(event));
    }

    @Test
    public void createOrder_processPaymentCompleted() {
        var created = createAndAssertOrder();
        var event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id(),
                created.amount()
        );
        assertDoesNotThrow(() -> {
            orderService.processPaymentCompleted(event);
            getAndAssertOrder(created.id(), OrderStatus.COMPLETED);
        });
    }

    @Test
    public void createOrder_processPaymentFailed() {
        var created = createAndAssertOrder();
        var event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id()
        );
        assertDoesNotThrow(() -> {
            orderService.processPaymentFailed(event);
            getAndAssertOrder(created.id(), OrderStatus.CANCELLED);
        });
    }

    @Test
    public void createOrder_processPaymentCompleted_deduplicationWorks() {
        var created = createAndAssertOrder();
        var event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id(),
                created.amount()
        );
        assertDoesNotThrow(() -> {
            orderService.processPaymentCompleted(event);
            orderService.processPaymentCompleted(event);
            getAndAssertOrder(created.id(), OrderStatus.COMPLETED);
        });
    }

    @Test
    public void createOrder_processPaymentFailed_deduplicationWorks() {
        var created = createAndAssertOrder();
        var event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id()
        );
        assertDoesNotThrow(() -> {
            orderService.processPaymentFailed(event);
            orderService.processPaymentFailed(event);
            getAndAssertOrder(created.id(), OrderStatus.CANCELLED);
        });
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
        assertEquals(1, outboxEventRepo.count());

        var orderEntity = orderRepo.findById(created.id());
        assertTrue(orderEntity.isPresent());

        return created;
    }

    /**
     * Get order by id and assert that it was found successfully.
     * This will also assert that the status of the order is as expected.
     * This will also assert that the processed event is present in the database.
     *
     * @param orderId        UUID of the order.
     * @param expectedStatus {@link OrderStatus} expected status of the order.
     * @throws OrderNotFoundException if the order is not found.
     */
    private void getAndAssertOrder(UUID orderId, OrderStatus expectedStatus) throws OrderNotFoundException {
        var order = orderService.findById(orderId);
        assertNotNull(order);
        assertEquals("Computer", order.product());
        assertEquals(199.0, order.amount());
        assertEquals(expectedStatus, order.status());

        assertEquals(1, processedEventRepo.count());
    }
}
