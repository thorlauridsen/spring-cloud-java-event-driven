package com.github.thorlauridsen;

import com.github.thorlauridsen.deduplication.ProcessedEventJpaRepo;
import com.github.thorlauridsen.exception.PaymentNotFoundException;
import com.github.thorlauridsen.model.event.OrderCreatedEvent;
import com.github.thorlauridsen.outbox.OutboxEventJpaRepo;
import com.github.thorlauridsen.persistence.PaymentJpaRepo;
import com.github.thorlauridsen.service.PaymentService;
import io.awspring.cloud.sns.core.SnsTemplate;
import java.util.UUID;
import lombok.val;
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

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceTest {

    @Autowired
    private OutboxEventJpaRepo outboxEventRepo;

    @Autowired
    private PaymentJpaRepo paymentRepo;

    @Autowired
    private PaymentService paymentService;

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
        assertThrows(PaymentNotFoundException.class, () -> paymentService.findByOrderId(UUID.randomUUID()));
    }

    @Test
    void processOrderCreated() {
        val event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Computer",
                199.0
        );
        paymentService.processOrderCreated(event);

        assertDoesNotThrow(() -> getAndAssertPayment(event.getOrderId()));
    }

    @Test
    void processOrderCreated_deduplicationWorks() {
        val event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Computer",
                199.0
        );
        paymentService.processOrderCreated(event);
        paymentService.processOrderCreated(event);

        assertDoesNotThrow(() -> getAndAssertPayment(event.getOrderId()));
    }

    /**
     * Get payment by order id and assert that it was found successfully.
     * This will also assert that the outbox, payment and processed event are present in the database.
     *
     * @param orderId UUID of the order related to the payment.
     * @throws PaymentNotFoundException if the payment is not found.
     */
    private void getAndAssertPayment(UUID orderId) throws PaymentNotFoundException {
        val payment = paymentService.findByOrderId(orderId);
        assertNotNull(payment);
        assertEquals(199.0, payment.amount());

        assertEquals(1, outboxEventRepo.count());
        assertEquals(1, paymentRepo.count());
        assertEquals(1, processedEventRepo.count());
    }
}
