package com.github.thorlauridsen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.dto.OrderCreateDto;
import com.github.thorlauridsen.dto.OrderDto;
import com.github.thorlauridsen.enumeration.OrderStatus;
import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.event.PaymentFailedEvent;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.persistence.OrderRepo;
import com.github.thorlauridsen.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.github.thorlauridsen.controller.BaseEndpoint.ORDER_BASE_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderControllerTest extends BaseMockMvc {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final OrderRepo orderRepo;
    private final OutboxRepo outboxRepo;

    @Autowired
    public OrderControllerTest(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            OrderService orderService,
            OrderRepo orderRepo,
            OutboxRepo outboxRepo
    ) {
        super(mockMvc);
        this.objectMapper = objectMapper;
        this.orderService = orderService;
        this.orderRepo = orderRepo;
        this.outboxRepo = outboxRepo;
    }

    @BeforeEach
    public void setup() {
        orderRepo.deleteAll();
        outboxRepo.deleteAll();
        assertEquals(0, orderRepo.count());
        assertEquals(0, outboxRepo.count());
    }

    @Test
    public void createOrder_PaymentCompleted() throws Exception {
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
    public void createOrder_PaymentFailed() throws Exception {
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
    private Order createAndAssertOrder() throws Exception {
        var order = new OrderCreateDto(
                "Computer",
                199.0
        );
        var json = objectMapper.writeValueAsString(order);
        var response = mockPost(json, ORDER_BASE_ENDPOINT + "/create");
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var responseJson = response.getContentAsString();
        var created = objectMapper.readValue(responseJson, OrderDto.class);

        assertNotNull(created);
        assertEquals("Computer", created.product());
        assertEquals(199.0, created.amount());

        assertEquals(1, orderRepo.count());
        assertEquals(1, outboxRepo.count());

        var orderEntity = orderRepo.findById(created.id());
        assertTrue(orderEntity.isPresent());

        return created.toModel();
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
