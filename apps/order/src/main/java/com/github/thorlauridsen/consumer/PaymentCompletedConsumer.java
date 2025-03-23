package com.github.thorlauridsen.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.event.PaymentCompletedEvent;
import com.github.thorlauridsen.service.OrderService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for the {@link PaymentCompletedEvent}.
 * This class will consume the specific event and process it in the {@link OrderService}.
 */
@Component
public class PaymentCompletedConsumer extends EventConsumer<PaymentCompletedEvent> {

    private final OrderService orderService;

    /**
     * Constructor for PaymentCompletedConsumer.
     *
     * @param objectMapper FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param orderService {@link OrderService} to process consumed events.
     */
    public PaymentCompletedConsumer(
            ObjectMapper objectMapper,
            OrderService orderService
    ) {
        super(objectMapper);
        this.orderService = orderService;
    }

    /**
     * Process the event in the {@link OrderService}.
     *
     * @param event {@link PaymentCompletedEvent} to process.
     */
    @Override
    protected void processEvent(PaymentCompletedEvent event) {
        orderService.processPaymentCompleted(event);
    }

    /**
     * Listen for messages on the SQS queue.
     *
     * @param json The JSON message from the SQS queue as a String.
     */
    @Override
    @SqsListener("${app.queues.payment-completed}")
    public void listen(String json) {
        super.listen(json);
    }

    /**
     * Get the class of the event that this consumer consumes.
     * This is used for FasterXML Jackson deserialization.
     *
     * @return {@link PaymentCompletedEvent}.
     */
    @Override
    protected Class<PaymentCompletedEvent> getEventClass() {
        return PaymentCompletedEvent.class;
    }
}

