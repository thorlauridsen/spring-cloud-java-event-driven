package com.github.thorlauridsen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.enumeration.OrderStatus;
import com.github.thorlauridsen.event.EventType;
import com.github.thorlauridsen.event.OrderCreatedEvent;
import com.github.thorlauridsen.model.Order;
import com.github.thorlauridsen.outbox.OutboxEntity;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.service.OrderOutboxPoller;
import com.github.thorlauridsen.service.OrderOutboxService;
import io.awspring.cloud.sns.core.SnsTemplate;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class OrderOutboxTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderOutboxPoller orderOutboxPoller;

    @Autowired
    private OrderOutboxService orderOutboxService;

    @Autowired
    private OutboxRepo outboxRepo;

    /**
     * Mocked SnsTemplate for testing.
     * Spring Cloud AWS SQS and SNS is disabled in the test profile.
     * So we need to mock this to ensure the producers still get a bean.
     */
    @MockitoBean
    private SnsTemplate snsTemplate;

    @BeforeEach
    public void setup() {
        outboxRepo.deleteAll();
        assertEquals(0, outboxRepo.count());
    }

    @Test
    public void prepareCompletedOrder_noEventExistsInOutbox() {
        prepareOrder(OrderStatus.COMPLETED);
        assertEquals(0, outboxRepo.count());
    }

    @Test
    public void prepareCancelledOrder_noEventExistsInOutbox() {
        prepareOrder(OrderStatus.CANCELLED);
        assertEquals(0, outboxRepo.count());
    }

    @Test
    public void prepareCreatedOrder_existsInOutbox() {
        prepareOrder(OrderStatus.CREATED);
        assertEquals(1, outboxRepo.count());
        assertEquals(1, outboxRepo.findAllByProcessedFalse().size());

        var event = outboxRepo.findAllByProcessedFalse().getFirst();

        assertNotNull(event);
        assertNotNull(event.getEventId());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getPayload());
        assertEquals(EventType.ORDER_CREATED, event.getEventType());
    }

    @Test
    public void processEvent_existsInOutbox() throws JsonProcessingException {
        var json = getOrderCreatedEventJson();
        processEvent(EventType.ORDER_CREATED, json);

        assertEquals(1, outboxRepo.count());
        assertEquals(0, outboxRepo.findAllByProcessedFalse().size());
    }

    @Test
    public void processEvent_invalidEventType_emptyOutbox() throws JsonProcessingException {
        var json = getOrderCreatedEventJson();
        processEvent(EventType.PAYMENT_COMPLETED, json);
        processEvent(EventType.PAYMENT_FAILED, json);

        assertEquals(0, outboxRepo.count());
    }

    @Test
    public void processEvent_invalidPayload_emptyOutbox() {
        processEvent(EventType.ORDER_CREATED, "invalidPayload");
        assertEquals(0, outboxRepo.count());
    }

    /**
     * Prepare an order with the given status.
     *
     * @param status The {@link OrderStatus} of the order.
     */
    private void prepareOrder(OrderStatus status) {
        var order = new Order(
                UUID.randomUUID(),
                OffsetDateTime.now(),
                status,
                "Computer",
                199.0
        );
        orderOutboxService.prepareEvent(order);
    }

    /**
     * Process an event in the outbox poller.
     * If the event type is invalid, the event should not be saved to the outbox.
     * If the event type is valid but the payload is invalid, the event should not be saved to the outbox.
     * If the event type and payload are valid, the event should be saved to the outbox.
     *
     * @param eventType The {@link EventType} of the event.
     * @param payload   The JSON payload of the event.
     */
    private void processEvent(
            EventType eventType,
            String payload
    ) {
        var entity = new OutboxEntity(
                UUID.randomUUID(),
                eventType,
                payload,
                OffsetDateTime.now(),
                false
        );
        orderOutboxPoller.process(entity);
    }

    /**
     * Get a JSON string of an {@link OrderCreatedEvent}.
     *
     * @return JSON string of an {@link OrderCreatedEvent}.
     */
    private String getOrderCreatedEventJson() throws JsonProcessingException {
        var event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Computer",
                199.0
        );
        return objectMapper.writeValueAsString(event);
    }
}
