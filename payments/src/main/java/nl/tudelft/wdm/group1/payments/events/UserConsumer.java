package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.payments.Payment;
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

    @KafkaListener(topics = {"creditSubstracted"})
    public void consume(Payment payment) {
        logger.info(String.format("#### -> Consumed message -> %s", payment));
        // Emit payment successful message
        producer.emitPaymentSuccessful(payment);
    }
}
