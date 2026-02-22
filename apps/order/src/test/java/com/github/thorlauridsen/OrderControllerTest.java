package com.github.thorlauridsen;

import com.github.thorlauridsen.deduplication.ProcessedEventJpaRepo;
import com.github.thorlauridsen.dto.OrderCreateDto;
import com.github.thorlauridsen.dto.OrderDto;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.model.enumeration.OrderStatus;
import com.github.thorlauridsen.model.event.PaymentCompletedEvent;
import com.github.thorlauridsen.model.event.PaymentFailedEvent;
import com.github.thorlauridsen.outbox.OutboxEventJpaRepo;
import com.github.thorlauridsen.persistence.OrderJpaRepo;
import com.github.thorlauridsen.service.OrderService;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import tools.jackson.databind.json.JsonMapper;

import java.util.UUID;

import static com.github.thorlauridsen.controller.BaseEndpoint.ORDER_BASE_ENDPOINT;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
class OrderControllerTest extends BaseControllerTest {

    private final JsonMapper jsonMapper;
    private final OrderService orderService;
    private final OrderJpaRepo orderRepo;
    private final OutboxEventJpaRepo outboxEventRepo;
    private final ProcessedEventJpaRepo processedEventRepo;

    /**
     * Mocked SnsTemplate for testing.
     * Spring Cloud AWS SQS and SNS is disabled in the test profile.
     * So we need to mock this to ensure the producers still get a bean.
     */
    @MockitoBean
    private SnsTemplate snsTemplate;

    @Autowired
    public OrderControllerTest(
            JsonMapper jsonMapper,
            OrderService orderService,
            OrderJpaRepo orderRepo,
            OutboxEventJpaRepo outboxEventRepo,
            ProcessedEventJpaRepo processedEventRepo
    ) {
        this.jsonMapper = jsonMapper;
        this.orderService = orderService;
        this.orderRepo = orderRepo;
        this.outboxEventRepo = outboxEventRepo;
        this.processedEventRepo = processedEventRepo;
    }

    @BeforeEach
    void setup() {
        orderRepo.deleteAll();
        outboxEventRepo.deleteAll();
        processedEventRepo.deleteAll();
        assertEquals(0, outboxEventRepo.count());
        assertEquals(0, orderRepo.count());
        assertEquals(0, processedEventRepo.count());
    }

    @Test
    void getOrder_noOrderExists() {
        val orderId = UUID.randomUUID();
        val response = get(ORDER_BASE_ENDPOINT + "/" + orderId);
        response.expectStatus().isNotFound();
    }

    @Test
    void createOrder_processPaymentCompleted() throws Exception {
        val created = postRequestAndAssertOrder();
        val event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id(),
                created.amount()
        );
        orderService.processPaymentCompleted(event);
        getRequestAndAssertOrder(created.id(), OrderStatus.COMPLETED);
    }

    @Test
    void createOrder_processPaymentFailed() throws Exception {
        val created = postRequestAndAssertOrder();
        val event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id()
        );
        orderService.processPaymentFailed(event);
        getRequestAndAssertOrder(created.id(), OrderStatus.CANCELLED);
    }

    /**
     * Send an HTTP POST request to create an order and assert that it was created successfully.
     * This will also serialize the request JSON to an {@link OrderDto} and assert its values.
     * This will assert that the order is present in the database.
     * It will also assert that the outbox event is present in the database.
     *
     * @return {@link Order} created order.
     */
    private Order postRequestAndAssertOrder() {
        val order = new OrderCreateDto(
                "Computer",
                199.0
        );
        val json = jsonMapper.writeValueAsString(order);
        val response = post(ORDER_BASE_ENDPOINT + "/create", json);
        response.expectStatus().isOk();

        val created = response.expectBody(OrderDto.class).returnResult().getResponseBody();

        assertNotNull(created);
        assertEquals("Computer", created.product());
        assertEquals(199.0, created.amount());

        assertEquals(1, orderRepo.count());
        assertEquals(1, outboxEventRepo.count());

        val orderEntity = orderRepo.findById(created.id());
        assertTrue(orderEntity.isPresent());

        return created.toModel();
    }

    /**
     * Send an HTTP GET request to retrieve an order and assert that it was retrieved successfully.
     * This will also serialize the response JSON to an {@link OrderDto} and assert its values.
     * This will also assert that the processed event is present in the database.
     *
     * @param orderId        UUID of the order.
     * @param expectedStatus {@link OrderStatus} expected status of the order.
     */
    private void getRequestAndAssertOrder(UUID orderId, OrderStatus expectedStatus) {
        val response = get(ORDER_BASE_ENDPOINT + "/" + orderId);
        response.expectStatus().isOk();

        val order = response.expectBody(OrderDto.class).returnResult().getResponseBody();

        assertNotNull(order);
        assertEquals("Computer", order.product());
        assertEquals(199.0, order.amount());
        assertEquals(expectedStatus, order.status());

        assertEquals(1, processedEventRepo.count());
    }
}
