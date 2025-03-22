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
import io.awspring.cloud.sns.core.SnsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.github.thorlauridsen.controller.BaseEndpoint.ORDER_BASE_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
public class OrderControllerTest extends BaseMockMvc {

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final OrderRepo orderRepo;
    private final OutboxRepo outboxRepo;

    /**
     * Mocked SnsTemplate for testing.
     * Spring Cloud AWS SQS and SNS is disabled in the test profile.
     * So we need to mock this to ensure the producers still get a bean.
     */
    @MockitoBean
    private SnsTemplate snsTemplate;

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
    public void getOrder_noOrderExists() throws Exception {
        var orderId = UUID.randomUUID();
        var response = mockGet(ORDER_BASE_ENDPOINT + "/" + orderId);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void createOrder_paymentCompleted() throws Exception {
        var created = postRequestAndAssertOrder();
        var event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                created.id(),
                created.amount()
        );
        orderService.processPaymentCompleted(event);
        getRequestAndAssertOrder(created.id(), OrderStatus.COMPLETED);
    }

    @Test
    public void createOrder_paymentFailed() throws Exception {
        var created = postRequestAndAssertOrder();
        var event = new PaymentFailedEvent(
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
    private Order postRequestAndAssertOrder() throws Exception {
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
     * Send an HTTP GET request to retrieve an order and assert that it was retrieved successfully.
     * This will also serialize the response JSON to an {@link OrderDto} and assert its values.
     *
     * @param orderId        UUID of the order.
     * @param expectedStatus {@link OrderStatus} expected status of the order.
     */
    private void getRequestAndAssertOrder(UUID orderId, OrderStatus expectedStatus) throws Exception {
        var response = mockGet(ORDER_BASE_ENDPOINT + "/" + orderId);
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var responseJson = response.getContentAsString();
        var order = objectMapper.readValue(responseJson, OrderDto.class);

        assertNotNull(order);
        assertEquals("Computer", order.product());
        assertEquals(199.0, order.amount());
        assertEquals(expectedStatus, order.status());
    }
}
