package com.github.thorlauridsen.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.event.OrderCreatedEvent;
import com.github.thorlauridsen.service.PaymentService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for the {@link OrderCreatedEvent}.
 * This class will consume the specific event and process it in the {@link PaymentService}.
 */
@Component
public class OrderCreatedConsumer extends EventConsumer<OrderCreatedEvent> {

    private final PaymentService paymentService;

    /**
     * Constructor for OrderCreatedConsumer.
     *
     * @param objectMapper   FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param paymentService {@link PaymentService} to process consumed events.
     */
    public OrderCreatedConsumer(
            ObjectMapper objectMapper,
            PaymentService paymentService
    ) {
        super(objectMapper);
        this.paymentService = paymentService;
    }

    /**
     * Process the event in the {@link PaymentService}.
     *
     * @param event {@link OrderCreatedEvent} to process.
     */
    @Override
    protected void processEvent(OrderCreatedEvent event) {
        paymentService.processOrderCreated(event);
    }

    /**
     * Listen for messages on the SQS queue.
     *
     * @param json The JSON message from the SQS queue as a String.
     */
    @Override
    @SqsListener("order-created-queue")
    public void listen(String json) {
        super.listen(json);
    }

    /**
     * Get the class of the event that this consumer consumes.
     * This is used for FasterXML Jackson deserialization.
     *
     * @return {@link OrderCreatedEvent}.
     */
    @Override
    protected Class<OrderCreatedEvent> getEventClass() {
        return OrderCreatedEvent.class;
    }
}
