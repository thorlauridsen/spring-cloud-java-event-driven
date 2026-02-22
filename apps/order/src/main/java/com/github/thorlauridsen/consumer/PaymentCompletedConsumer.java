package com.github.thorlauridsen.consumer;

import com.github.thorlauridsen.event.PaymentCompletedEventDto;
import com.github.thorlauridsen.service.OrderService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Consumer for the {@link PaymentCompletedEventDto}.
 * This class will consume the specific event and process it in the {@link OrderService}.
 */
@Component
@Slf4j
public class PaymentCompletedConsumer extends BaseEventConsumer<PaymentCompletedEventDto> {

    private final OrderService orderService;

    /**
     * Constructor for PaymentCompletedConsumer.
     *
     * @param jsonMapper   FasterXML Jackson {@link JsonMapper} for serialization/deserialization.
     * @param orderService {@link OrderService} to process consumed events.
     */
    public PaymentCompletedConsumer(
            JsonMapper jsonMapper,
            OrderService orderService
    ) {
        super(jsonMapper);
        this.orderService = orderService;
    }

    /**
     * Process the event in the {@link OrderService}.
     * The event is converted to a model and processed in the {@link OrderService}.
     *
     * @param event {@link PaymentCompletedEventDto} to process.
     */
    @Override
    protected void processEvent(PaymentCompletedEventDto event) {
        try {
            orderService.processPaymentCompleted(event.toModel());
        } catch (Exception ex) {
            log.error("Failed to process payment completed event: {} {}", event.getEventType(), event.getId(), ex);
        }
    }

    /**
     * Listen for messages on the SQS queue.
     * The queue ARN is defined in application.yml.
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
     * @return {@link PaymentCompletedEventDto}.
     */
    @Override
    protected Class<PaymentCompletedEventDto> getEventClass() {
        return PaymentCompletedEventDto.class;
    }
}
