package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.common.PaymentsTopics;
import nl.tudelft.wdm.group1.common.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Value("${spring.kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, Payment> kafkaTemplate;

    public void emitPaymentCreated(Payment payment) {
        logger.info(String.format("#### -> Producing message -> %s", payment));
        this.kafkaTemplate.send(PaymentsTopics.PAYMENT_CREATED, payment);
    }

    public void emitPaymentSuccessful(Payment payment) {
        logger.info(String.format("#### -> Producing message -> %s", payment));
        this.kafkaTemplate.send(PaymentsTopics.PAYMENT_SUCCESSFUL, payment);
    }

    public void emitPaymentDeleted(Payment payment) {
        // TODO: adds the amount of the order to the user's credit
        logger.info(String.format("#### -> Producing message -> %s", payment));
        this.kafkaTemplate.send(PaymentsTopics.PAYMENT_DELETED, payment);
    }
}