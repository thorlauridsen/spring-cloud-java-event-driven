package com.github.thorlauridsen.producer;

import com.github.thorlauridsen.event.OrderCreatedEvent;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.beans.factory.annotation.Value;
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
     * @param topicArn    The SNS topic ARN to publish the event to.
     */
    public OrderCreatedProducer(
            SnsTemplate snsTemplate,
            @Value("${app.topics.order-created}") String topicArn
    ) {
        super(snsTemplate, topicArn);
    }
}
