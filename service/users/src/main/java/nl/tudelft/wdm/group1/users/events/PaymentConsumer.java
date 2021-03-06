package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.exception.CreditChangeInvalidException;
import nl.tudelft.wdm.group1.common.exception.InsufficientCreditException;
import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.Payment;
import nl.tudelft.wdm.group1.common.model.User;
import nl.tudelft.wdm.group1.common.topic.PaymentsTopics;
import nl.tudelft.wdm.group1.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumer {
    private final UserRepository userRepository;
    private final PaymentProducer paymentProducer;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public PaymentConsumer(UserRepository userRepository, PaymentProducer paymentProducer) {
        this.userRepository = userRepository;
        this.paymentProducer = paymentProducer;
    }

    @KafkaListener(topicPartitions = {
            @TopicPartition(topic = PaymentsTopics.PAYMENT_CREATED, partitions = "#{instance_id}")
    })
    public void consumePaymentCreated(Payment payment) throws ResourceNotFoundException, CreditChangeInvalidException {
        logger.info("Consuming [{}] -> {}", PaymentsTopics.PAYMENT_CREATED, payment);
        try {
            User user = userRepository.findOrElseThrow(payment.getUserId());
            user.subtractCredit(payment.getAmount());
            userRepository.save(user);
            paymentProducer.emitCreditSubtractedForPayment(payment);
        } catch (InsufficientCreditException e) {
            paymentProducer.emitCreditSubtractionForPaymentFailed(payment);
        } catch (CreditChangeInvalidException | ResourceNotFoundException e) {
            throw e;
        }
    }
}
