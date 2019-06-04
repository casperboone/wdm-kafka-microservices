package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.common.PaymentsTopics;
import nl.tudelft.wdm.group1.common.Payment;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.UsersTopics;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    private final PaymentRepository paymentRepository;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public Consumer(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @KafkaListener(topics = {PaymentsTopics.PAYMENT_CREATED})
    public void consume(Payment payment) {
        logger.info("Consuming [{}] -> {}", PaymentsTopics.PAYMENT_CREATED, payment);
        paymentRepository.save(payment);
    }

    @KafkaListener(topics = {PaymentsTopics.PAYMENT_DELETED})
    public void consumePaymentDeleted(Payment payment) throws ResourceNotFoundException {
        logger.info("Consuming [{},{}] -> {}", PaymentsTopics.PAYMENT_DELETED, UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_FAILED, payment);
        paymentRepository.delete(paymentRepository.findOrElseThrow(payment.getOrderId()));
    }
}
