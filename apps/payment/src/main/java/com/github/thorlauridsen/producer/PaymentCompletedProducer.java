package com.github.thorlauridsen.producer;

import com.github.thorlauridsen.event.PaymentCompletedEvent;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Producer for the {@link PaymentCompletedEvent}.
 * This class will publish the specific event to the SNS topic.
 */
@Service
public class PaymentCompletedProducer extends EventProducer<PaymentCompletedEvent> {

    /**
     * Constructor for PaymentCompletedProducer.
     *
     * @param snsTemplate {@link SnsTemplate} for publishing events.
     * @param topicArn    The SNS topic ARN to publish the event to.
     */
    public PaymentCompletedProducer(
            SnsTemplate snsTemplate,
            @Value("${app.topics.payment-completed}") String topicArn
    ) {
        super(snsTemplate, topicArn);
    }
}
