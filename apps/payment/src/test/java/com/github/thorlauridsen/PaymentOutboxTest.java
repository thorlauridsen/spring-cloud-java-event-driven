package com.github.thorlauridsen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.model.enumeration.EventType;
import com.github.thorlauridsen.model.enumeration.PaymentStatus;
import com.github.thorlauridsen.event.PaymentCompletedEventDto;
import com.github.thorlauridsen.event.PaymentFailedEventDto;
import com.github.thorlauridsen.model.Payment;
import com.github.thorlauridsen.model.event.OutboxEvent;
import com.github.thorlauridsen.outbox.OutboxEventJpaRepo;
import com.github.thorlauridsen.producer.PaymentOutboxPoller;
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
    private OutboxEventJpaRepo outboxEventRepo;

    /**
     * Mocked SnsTemplate for testing.
     * Spring Cloud AWS SQS and SNS is disabled in the test profile.
     * So we need to mock this to ensure the producers still get a bean.
     */
    @MockitoBean
    private SnsTemplate snsTemplate;

    @BeforeEach
    public void setup() {
        outboxEventRepo.deleteAll();
        assertEquals(0, outboxEventRepo.count());
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
    public void processEvent_invalidEventType_emptyOutbox() throws JsonProcessingException {
        var json = getPaymentCompletedEventJson();
        processEvent(EventType.ORDER_CREATED, json);

        assertEquals(0, outboxEventRepo.count());
    }

    @Test
    public void processEvent_invalidPayload_emptyOutbox() {
        processEvent(EventType.PAYMENT_COMPLETED, "invalidPayload");
        processEvent(EventType.PAYMENT_FAILED, "invalidPayload");

        assertEquals(0, outboxEventRepo.count());
    }

    /**
     * Prepare a payment with the given status.
     * Assert that the event is saved to the outbox.
     *
     * @param status            The {@link PaymentStatus} of the payment.
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

        assertEquals(1, outboxEventRepo.count());
        assertEquals(1, outboxEventRepo.findAllByProcessedFalse().size());

        var event = outboxEventRepo.findAllByProcessedFalse().getFirst();

        assertNotNull(event);
        assertNotNull(event.getEventId());
        assertNotNull(event.getCreatedAt());
        assertNotNull(event.getPayload());
        assertEquals(expectedEventType, event.getEventType());

        paymentOutboxPoller.pollOutboxTable();
        assertEquals(0, outboxEventRepo.findAllByProcessedFalse().size());
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
        var entity = new OutboxEvent(
                UUID.randomUUID(),
                eventType,
                payload,
                OffsetDateTime.now(),
                false
        );
        paymentOutboxPoller.process(entity);
    }

    /**
     * Get a JSON string of an {@link PaymentFailedEventDto}.
     *
     * @return JSON string of an {@link PaymentFailedEventDto}.
     */
    private String getPaymentFailedEventJson() throws JsonProcessingException {
        var event = new PaymentFailedEventDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        return objectMapper.writeValueAsString(event);
    }

    /**
     * Get a JSON string of an {@link PaymentCompletedEventDto}.
     *
     * @return JSON string of an {@link PaymentCompletedEventDto}.
     */
    private String getPaymentCompletedEventJson() throws JsonProcessingException {
        var event = new PaymentCompletedEventDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                199.0
        );
        return objectMapper.writeValueAsString(event);
    }
}
