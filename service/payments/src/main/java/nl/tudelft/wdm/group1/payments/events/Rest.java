package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.common.payload.*;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(topics = RestTopics.REQUEST)
public class Rest {

    private final PaymentRepository paymentRepository;
    private KafkaTemplate<String, Object> rest;

    @Autowired
    public Rest(PaymentRepository paymentRepository, KafkaTemplate<String, Object> rest) {
        this.paymentRepository = paymentRepository;
        this.rest = rest;
        rest.setDefaultTopic(RestTopics.RESPONSE);
    }

    @KafkaHandler
    public void consumePaymentAdd(PaymentAddPayload payload) {
        Payment payment = new Payment(payload.getUserId(), payload.getOrderId(), payload.getAmount());
        paymentRepository.addOrReplace(payment);
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), payment));
    }

    @KafkaHandler
    public void consumePaymentDelete(PaymentDeletePayload payload) {
        try {
            Payment payment = paymentRepository.find(payload.getOrderId());
            paymentRepository.remove(payload.getOrderId());
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), payment));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumePaymentGet(PaymentGetPayload payload) {
        try {
            Payment payment = paymentRepository.find(payload.getOrderId());
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), payment));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
    }
}


