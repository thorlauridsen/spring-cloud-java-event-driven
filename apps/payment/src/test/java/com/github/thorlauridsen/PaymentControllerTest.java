package com.github.thorlauridsen;

import com.github.thorlauridsen.deduplication.ProcessedEventJpaRepo;
import com.github.thorlauridsen.dto.PaymentDto;
import com.github.thorlauridsen.model.event.OrderCreatedEvent;
import com.github.thorlauridsen.outbox.OutboxEventJpaRepo;
import com.github.thorlauridsen.persistence.PaymentJpaRepo;
import com.github.thorlauridsen.service.PaymentService;
import io.awspring.cloud.sns.core.SnsTemplate;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;

import static com.github.thorlauridsen.controller.BaseEndpoint.PAYMENT_BASE_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
class PaymentControllerTest extends BaseControllerTest {

    private final OutboxEventJpaRepo outboxEventRepo;
    private final PaymentJpaRepo paymentRepo;
    private final PaymentService paymentService;
    private final ProcessedEventJpaRepo processedEventRepo;

    /**
     * Mocked SnsTemplate for testing.
     * Spring Cloud AWS SQS and SNS is disabled in the test profile.
     * So we need to mock this to ensure the producers still get a bean.
     */
    @MockitoBean
    private SnsTemplate snsTemplate;

    @Autowired
    public PaymentControllerTest(
            OutboxEventJpaRepo outboxEventRepo,
            PaymentJpaRepo paymentRepo,
            PaymentService paymentService,
            ProcessedEventJpaRepo processedEventRepo
    ) {
        this.outboxEventRepo = outboxEventRepo;
        this.paymentRepo = paymentRepo;
        this.paymentService = paymentService;
        this.processedEventRepo = processedEventRepo;
    }

    @BeforeEach
    void setup() {
        outboxEventRepo.deleteAll();
        paymentRepo.deleteAll();
        processedEventRepo.deleteAll();
        assertEquals(0, outboxEventRepo.count());
        assertEquals(0, paymentRepo.count());
        assertEquals(0, processedEventRepo.count());
    }

    @Test
    void getPayment_noPaymentExists() {
        val orderId = UUID.randomUUID();
        val response = get(PAYMENT_BASE_ENDPOINT + "/" + orderId);
        response.expectStatus().isNotFound();
    }

    @Test
    void processOrderCreated_getPayment_paymentExists() {
        val event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Computer",
                199.0
        );
        paymentService.processOrderCreated(event);

        val response = get(PAYMENT_BASE_ENDPOINT + "/" + event.getOrderId());
        response.expectStatus().isOk();

        val payment = response.expectBody(PaymentDto.class).returnResult().getResponseBody();

        assertNotNull(payment);
        assertEquals(event.getOrderId(), payment.orderId());
        assertEquals(199.0, payment.amount());

        assertEquals(1, outboxEventRepo.count());
        assertEquals(1, paymentRepo.count());
        assertEquals(1, processedEventRepo.count());
    }
}
