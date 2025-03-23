package com.github.thorlauridsen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.enumeration.PaymentStatus;
import com.github.thorlauridsen.event.EventType;
import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.event.PaymentFailedEvent;
import com.github.thorlauridsen.model.Payment;
import com.github.thorlauridsen.outbox.OutboxEntity;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.service.PaymentOutboxPoller;
import com.github.thorlauridsen.service.PaymentOutboxService;
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
public class PaymentOutboxTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentOutboxPoller paymentOutboxPoller;

    @Autowired
    private PaymentOutboxService paymentOutboxService;

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
    public void prepareCompletedPayment_existsInOutbox() {
        preparePaymentAndAssert(PaymentStatus.COMPLETED, EventType.PAYMENT_COMPLETED);
    }

    @Test
    public void prepareCancelledPayment_existsInOutbox() {
        preparePaymentAndAssert(PaymentStatus.FAILED, EventType.PAYMENT_FAILED);
    }

    @Test
    public void processEvent_existsInOutbox() throws JsonProcessingException {
        var paymentCompletedEventJson = getPaymentCompletedEventJson();
        var paymentFailedEventJson = getPaymentFailedEventJson();

        processEvent(EventType.PAYMENT_COMPLETED, paymentCompletedEventJson);
        processEvent(EventType.PAYMENT_FAILED, paymentFailedEventJson);

        assertEquals(2, outboxRepo.count());
        assertEquals(0, outboxRepo.findAllByProcessedFalse().size());
    }

    @Test
    public void processEvent_invalidEventType_emptyOutbox() throws JsonProcessingException {
        var json = getPaymentCompletedEventJson();
        processEvent(EventType.ORDER_CREATED, json);

        assertEquals(0, outboxRepo.count());
    }

    @Test
    public void processEvent_invalidPayload_emptyOutbox() {
        processEvent(EventType.PAYMENT_COMPLETED, "invalidPayload");
        processEvent(EventType.PAYMENT_FAILED, "invalidPayload");

        assertEquals(0, outboxRepo.count());
    }

    /**
     * Prepare a payment with the given status.
     * Assert that the event is saved to the outbox.
     *
     * @param status The {@link PaymentStatus} of the payment.
     * @param expectedEventType The expected {@link EventType} of the event.
     */
    private void preparePaymentAndAssert(
            PaymentStatus status,
            EventType expectedEventType
    ) {
        var payment = new Payment(
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.now(),
                status,
                199.0
        );
        paymentOutboxService.prepareEvent(payment);

        assertEquals(1, outboxRepo.count());
        assertEquals(1, outboxRepo.findAllByProcessedFalse().size());

        var event = outboxRepo.findAllByProcessedFalse().getFirst();

        assertNotNull(event);
        assertNotNull(event.getEventId());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getPayload());
        assertEquals(expectedEventType, event.getEventType());

        paymentOutboxPoller.pollOutboxTable();
        assertEquals(0, outboxRepo.findAllByProcessedFalse().size());
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
        paymentOutboxPoller.process(entity);
    }

    /**
     * Get a JSON string of an {@link PaymentFailedEvent}.
     *
     * @return JSON string of an {@link PaymentFailedEvent}.
     */
    private String getPaymentFailedEventJson() throws JsonProcessingException {
        var event = new PaymentFailedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        return objectMapper.writeValueAsString(event);
    }

    /**
     * Get a JSON string of an {@link PaymentCompletedEvent}.
     *
     * @return JSON string of an {@link PaymentCompletedEvent}.
     */
    private String getPaymentCompletedEventJson() throws JsonProcessingException {
        var event = new PaymentCompletedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                199.0
        );
        return objectMapper.writeValueAsString(event);
    }
}
