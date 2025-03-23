package com.github.thorlauridsen.producer;

import com.github.thorlauridsen.event.PaymentFailedEvent;
import io.awspring.cloud.sns.core.SnsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Producer for the {@link PaymentFailedEvent}.
 * This class will publish the specific event to the SNS topic.
 */
@Service
public class PaymentFailedProducer extends EventProducer<PaymentFailedEvent> {

    /**
     * Constructor for PaymentFailedProducer.
     *
     * @param snsTemplate {@link SnsTemplate} to publish the event to the SNS topic.
     * @param topicArn    The SNS topic ARN to publish the event to.
     */
    public PaymentFailedProducer(
            SnsTemplate snsTemplate,
            @Value("${app.topics.payment-failed}") String topicArn
    ) {
        super(snsTemplate, topicArn);
    }
}
