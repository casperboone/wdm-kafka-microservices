package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.topic.UsersTopics;
import nl.tudelft.wdm.group1.common.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
    private static final Logger logger = LoggerFactory.getLogger(PaymentProducer.class);

    @Autowired
    private KafkaTemplate<String, Payment> kafkaTemplate;

    public void emitCreditSubtractedForPayment(Payment payment) {
        logger.info("Producing [{}] -> {}", UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_SUCCESSFUL, payment);
        this.kafkaTemplate.send(UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_SUCCESSFUL, payment);
    }

    public void emitCreditSubtractionForPaymentFailed(Payment payment) {
        logger.info("Producing [{}] -> {}", UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_FAILED, payment);
        this.kafkaTemplate.send(UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_FAILED, payment);
    }
}
