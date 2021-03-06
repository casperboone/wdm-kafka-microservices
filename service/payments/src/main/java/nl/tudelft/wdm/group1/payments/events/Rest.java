package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.common.KafkaErrorResponse;
import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.Payment;
import nl.tudelft.wdm.group1.common.payload.PaymentAddPayload;
import nl.tudelft.wdm.group1.common.payload.PaymentDeletePayload;
import nl.tudelft.wdm.group1.common.payload.PaymentGetPayload;
import nl.tudelft.wdm.group1.common.topic.RestTopics;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(topics = RestTopics.PAYMENT_REQUEST)
public class Rest {

    private final PaymentRepository paymentRepository;
    private KafkaTemplate<String, Object> rest;
    private Producer producer;

    @Autowired
    public Rest(PaymentRepository paymentRepository, KafkaTemplate<String, Object> rest, Producer producer) {
        this.paymentRepository = paymentRepository;
        this.rest = rest;
        this.producer = producer;
        rest.setDefaultTopic(RestTopics.RESPONSE);
    }

    @KafkaHandler
    public void consumePaymentAdd(PaymentAddPayload payload) {
        Payment payment = new Payment(payload.getUserId(), payload.getOrderId(), payload.getAmount());
        producer.emitPaymentCreated(payment);
        rest.sendDefault(payload.getPartition(), "", new KafkaResponse<>(payload.getRequestId(), payment));
    }

    @KafkaHandler
    public void consumePaymentDelete(PaymentDeletePayload payload) {
        try {
            Payment payment = paymentRepository.findOrElseThrow(payload.getOrderId());
            producer.emitPaymentDeleted(payment);
            rest.sendDefault(payload.getPartition(), "", new KafkaResponse<>(payload.getRequestId(), payment));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(payload.getPartition(), "", new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumePaymentGet(PaymentGetPayload payload) {
        try {
            Payment payment = paymentRepository.findOrElseThrow(payload.getOrderId());
            rest.sendDefault(payload.getPartition(), "", new KafkaResponse<>(payload.getRequestId(), payment));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(payload.getPartition(), "", new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
    }
}


