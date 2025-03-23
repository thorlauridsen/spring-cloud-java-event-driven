package com.github.thorlauridsen;

import com.github.thorlauridsen.deduplication.ProcessedEventRepo;
import com.github.thorlauridsen.event.OrderCreatedEvent;
import com.github.thorlauridsen.exception.PaymentNotFoundException;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.persistence.PaymentRepo;
import com.github.thorlauridsen.service.PaymentService;
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

@SpringBootTest
@ActiveProfiles("test")
public class PaymentServiceTest {

    @Autowired
    private OutboxRepo outboxRepo;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private PaymentService paymentService;

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
        outboxRepo.deleteAll();
        paymentRepo.deleteAll();
        processedEventRepo.deleteAll();
        assertEquals(0, outboxRepo.count());
        assertEquals(0, paymentRepo.count());
        assertEquals(0, processedEventRepo.count());
    }

    @Test
    public void getPayment_noPaymentExists() {
        assertThrows(PaymentNotFoundException.class, () -> paymentService.findByOrderId(UUID.randomUUID()));
    }

    @Test
    public void processOrderCreated() {
        var event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Computer",
                199.0
        );
        paymentService.processOrderCreated(event);

        assertDoesNotThrow(() -> getAndAssertPayment(event.getOrderId()));
    }

    @Test
    public void processOrderCreated_deduplicationWorks() {
        var event = new OrderCreatedEvent(
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
        var payment = paymentService.findByOrderId(orderId);
        assertNotNull(payment);
        assertEquals(199.0, payment.amount());

        assertEquals(1, outboxRepo.count());
        assertEquals(1, paymentRepo.count());
        assertEquals(1, processedEventRepo.count());
    }
}
