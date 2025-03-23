package com.github.thorlauridsen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.deduplication.ProcessedEventJpaRepo;
import com.github.thorlauridsen.dto.PaymentDto;
import com.github.thorlauridsen.model.event.OrderCreatedEvent;
import com.github.thorlauridsen.outbox.OutboxEventJpaRepo;
import com.github.thorlauridsen.persistence.PaymentJpaRepo;
import com.github.thorlauridsen.service.PaymentService;
import io.awspring.cloud.sns.core.SnsTemplate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.thorlauridsen.controller.BaseEndpoint.PAYMENT_BASE_ENDPOINT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
public class PaymentControllerTest extends BaseMockMvc {

    private final ObjectMapper objectMapper;
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
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            OutboxEventJpaRepo outboxEventRepo,
            PaymentJpaRepo paymentRepo,
            PaymentService paymentService,
            ProcessedEventJpaRepo processedEventRepo
    ) {
        super(mockMvc);
        this.objectMapper = objectMapper;
        this.outboxEventRepo = outboxEventRepo;
        this.paymentRepo = paymentRepo;
        this.paymentService = paymentService;
        this.processedEventRepo = processedEventRepo;
    }

    @BeforeEach
    public void setup() {
        outboxEventRepo.deleteAll();
        paymentRepo.deleteAll();
        processedEventRepo.deleteAll();
        assertEquals(0, outboxEventRepo.count());
        assertEquals(0, paymentRepo.count());
        assertEquals(0, processedEventRepo.count());
    }

    @Test
    public void getPayment_noPaymentExists() throws Exception {
        var orderId = UUID.randomUUID();
        var response = mockGet(PAYMENT_BASE_ENDPOINT + "/" + orderId);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void processOrderCreated_getPayment_paymentExists() throws Exception {
        var event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Computer",
                199.0
        );
        paymentService.processOrderCreated(event);

        var response = mockGet(PAYMENT_BASE_ENDPOINT + "/" + event.getOrderId());
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        var responseJson = response.getContentAsString();
        var payment = objectMapper.readValue(responseJson, PaymentDto.class);

        assertNotNull(payment);
        assertEquals(event.getOrderId(), payment.orderId());
        assertEquals(199.0, payment.amount());

        assertEquals(1, outboxEventRepo.count());
        assertEquals(1, paymentRepo.count());
        assertEquals(1, processedEventRepo.count());
    }
}
