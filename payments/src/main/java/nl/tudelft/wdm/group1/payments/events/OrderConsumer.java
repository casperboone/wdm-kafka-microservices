package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.payments.Payment;
import nl.tudelft.wdm.group1.payments.Order;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrderConsumer {
    private final PaymentRepository paymentRepository;
    private final Producer producer;

    private final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    public OrderConsumer(PaymentRepository paymentRepository, Producer producer) {
        this.paymentRepository = paymentRepository;
        this.producer = producer;
    }

    @KafkaListener(topics = {"orderCheckout"})
    public void consume(Order order) {
        logger.info(String.format("#### -> Consumed message -> %s", order));
        // Triggers new payment creation
        Payment payment = new Payment(order.getUserId(), order.getId(), order.getAmount());
        producer.emitPaymentCreated(payment);
    }
}
