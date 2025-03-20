package com.github.thorlauridsen;

import com.github.thorlauridsen.event.OrderCreatedEvent;
import com.github.thorlauridsen.outbox.OutboxRepo;
import com.github.thorlauridsen.persistence.PaymentRepo;
import com.github.thorlauridsen.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private OutboxRepo outboxRepo;

    @BeforeEach
    public void setup() {
        paymentRepo.deleteAll();
        outboxRepo.deleteAll();
        assertEquals(0, paymentRepo.count());
        assertEquals(0, outboxRepo.count());
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
        assertEquals(1, paymentRepo.count());
        assertEquals(1, outboxRepo.count());
    }
}
