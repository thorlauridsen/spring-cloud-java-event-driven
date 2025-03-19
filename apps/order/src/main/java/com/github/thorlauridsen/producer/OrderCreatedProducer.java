package com.github.thorlauridsen.producer;

import com.github.thorlauridsen.event.OrderCreatedEvent;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer for the {@link OrderCreatedEvent}.
 * This class will publish the specific event to the SNS topic.
 */
@Service
public class OrderCreatedProducer extends EventProducer<OrderCreatedEvent> {

    /**
     * Constructor for OrderCreatedProducer.
     *
     * @param snsTemplate {@link SnsTemplate} to send the event to the SNS topic.
     */
    public OrderCreatedProducer(SnsTemplate snsTemplate) {
        super(snsTemplate, "arn:aws:sns:us-east-1:000000000000:order-created-topic");
    }
}
