package com.github.thorlauridsen.producer;

import com.github.thorlauridsen.event.PaymentCompletedEvent;
import io.awspring.cloud.sns.core.SnsTemplate;
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
     */
    public PaymentCompletedProducer(SnsTemplate snsTemplate) {
        super(snsTemplate, "arn:aws:sns:us-east-1:000000000000:payment-completed-topic");
    }
}
