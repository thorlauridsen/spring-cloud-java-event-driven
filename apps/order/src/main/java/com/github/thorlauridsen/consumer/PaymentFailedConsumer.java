package com.github.thorlauridsen.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.thorlauridsen.event.PaymentFailedEvent;
import com.github.thorlauridsen.service.OrderService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for the {@link PaymentFailedEvent}.
 * This class will consume the specific event and process it in the {@link OrderService}.
 */
@Component
public class PaymentFailedConsumer extends EventConsumer<PaymentFailedEvent> {

    private final OrderService orderService;

    /**
     * Constructor for PaymentFailedConsumer.
     *
     * @param objectMapper FasterXML Jackson {@link ObjectMapper} for serialization/deserialization.
     * @param orderService {@link OrderService} to process consumed events.
     */
    public PaymentFailedConsumer(
            ObjectMapper objectMapper,
            OrderService orderService
    ) {
        super(objectMapper);
        this.orderService = orderService;
    }

    /**
     * Process the event in the {@link OrderService}.
     *
     * @param event {@link PaymentFailedEvent} to process.
     */
    @Override
    protected void processEvent(PaymentFailedEvent event) {
        try {
            orderService.processPaymentFailed(event);
        } catch (Exception ex) {
            logger.error("Failed to process payment failed event: {} {}", event.getEventType(), event.getId(), ex);
        }
    }

    /**
     * Listen for messages on the SQS queue.
     *
     * @param json The JSON message from the SQS queue as a String.
     */
    @Override
    @SqsListener("${app.queues.payment-failed}")
    public void listen(String json) {
        super.listen(json);
    }

    /**
     * Get the class of the event that this consumer consumes.
     * This is used for FasterXML Jackson deserialization.
     *
     * @return {@link PaymentFailedEvent}.
     */
    @Override
    protected Class<PaymentFailedEvent> getEventClass() {
        return PaymentFailedEvent.class;
    }
}
