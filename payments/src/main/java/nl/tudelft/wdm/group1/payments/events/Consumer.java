package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.payments.Payment;
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

    @KafkaListener(topics = {"paymentCreated"})
    public void consume(Payment payment) {
        logger.info(String.format("#### -> Consumed message -> %s", payment));
        paymentRepository.addOrReplace(payment);
    }

    @KafkaListener(topics = {"paymentDeleted"})
    public void consumePaymentDeleted(Payment payment) throws ResourceNotFoundException {
        logger.info(String.format("#### -> Consumed message -> %s", payment));
        paymentRepository.remove(payment.getId());
    }
}
