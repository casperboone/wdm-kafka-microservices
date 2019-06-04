package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
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

    @KafkaListener(topics = {PaymentsTopics.PAYMENT_CREATED})
    public void consumePaymentCreated(Payment payment) throws ResourceNotFoundException, CreditChangeInvalidException {
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
