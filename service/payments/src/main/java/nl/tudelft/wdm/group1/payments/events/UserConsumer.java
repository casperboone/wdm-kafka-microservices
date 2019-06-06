package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.topic.UsersTopics;
import nl.tudelft.wdm.group1.common.model.Payment;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserConsumer {
    private final PaymentRepository paymentRepository;
    private final Producer producer;

    private final Logger logger = LoggerFactory.getLogger(UserConsumer.class);

    public UserConsumer(PaymentRepository paymentRepository, Producer producer) {
        this.paymentRepository = paymentRepository;
        this.producer = producer;
    }

    @KafkaListener(topics = {UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_SUCCESSFUL})
    public void consumePaymentSuccessful(Payment payment) {
        logger.info("Consuming [{}] -> {}", UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_SUCCESSFUL, payment);
        // Emit payment successful message
        producer.emitPaymentSuccessful(payment);
    }

    @KafkaListener(topics = {UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_FAILED})
    public void consumePaymentFailed(Payment payment) throws ResourceNotFoundException {
        logger.info("Consuming [{}] -> {}", UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_FAILED, payment);
        // Emit payment failed message
        producer.emitPaymentFailed(payment);
        paymentRepository.delete(paymentRepository.findOrElseThrow(payment.getOrderId()));
    }
}
