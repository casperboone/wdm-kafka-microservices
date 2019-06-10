package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.common.model.Payment;
import nl.tudelft.wdm.group1.common.topic.PaymentsTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, Payment> kafkaTemplate;

    public void emitPaymentCreated(Payment payment) {
        logger.info("Producing [{}] -> {}", PaymentsTopics.PAYMENT_CREATED, payment);
        this.kafkaTemplate.send(PaymentsTopics.PAYMENT_CREATED, payment.getUserId().toString(), payment);
    }

    public void emitPaymentSuccessful(Payment payment) {
        logger.info("Producing [{}] -> {}", PaymentsTopics.PAYMENT_SUCCESSFUL, payment);
        this.kafkaTemplate.send(PaymentsTopics.PAYMENT_SUCCESSFUL, payment.getUserId().toString(), payment);
    }

    public void emitPaymentFailed(Payment payment) {
        logger.info("Producing [{}] -> {}", PaymentsTopics.PAYMENT_FAILED, payment);
        this.kafkaTemplate.send(PaymentsTopics.PAYMENT_FAILED, payment.getUserId().toString(), payment);
    }

    public void emitPaymentDeleted(Payment payment) {
        // TODO: adds the amount of the order to the user's credit
        logger.info("Producing [{}] -> {}", PaymentsTopics.PAYMENT_DELETED, payment);
        this.kafkaTemplate.send(PaymentsTopics.PAYMENT_DELETED, payment.getUserId().toString(), payment);
    }
}
