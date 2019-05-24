package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.CreditChangeInvalidException;
import nl.tudelft.wdm.group1.common.InsufficientCreditException;
import nl.tudelft.wdm.group1.common.Payment;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
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

    @KafkaListener(topics = "paymentCreated")
    public void consumePaymentCreated(Payment payment) throws ResourceNotFoundException, CreditChangeInvalidException {
        try {
            userRepository.find(payment.getUserId()).subtractCredit(payment.getAmount());
            paymentProducer.emitCreditSubtractedForPayment(payment);
        } catch (InsufficientCreditException e) {
            paymentProducer.emitCreditSubtractionForPaymentFailed(payment);
        } catch (CreditChangeInvalidException | ResourceNotFoundException e) {
            throw e;
        }
    }
}
