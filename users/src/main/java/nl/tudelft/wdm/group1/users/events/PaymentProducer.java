package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, Payment> kafkaTemplate;

    public void emitCreditSubtractedForPayment(Payment payment) {
        logger.info(String.format("#### -> Producing message -> %s", payment));
        this.kafkaTemplate.send("creditSubtractedForPayment", payment);
    }

    public void emitCreditSubtractionForPaymentFailed(Payment payment) {
        logger.info(String.format("#### -> Producing message -> %s", payment));
        this.kafkaTemplate.send("creditSubtractionForPaymentFailed", payment);
    }
}
