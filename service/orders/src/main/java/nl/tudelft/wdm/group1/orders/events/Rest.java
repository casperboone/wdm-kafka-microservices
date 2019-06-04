package nl.tudelft.wdm.group1.orders.events;

import nl.tudelft.wdm.group1.common.KafkaErrorResponse;
import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.Order;
import nl.tudelft.wdm.group1.common.payload.*;
import nl.tudelft.wdm.group1.common.topics.RestTopics;
import nl.tudelft.wdm.group1.orders.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(topics = RestTopics.REQUEST)
public class Rest {

    private final OrderRepository orderRepository;
    private KafkaTemplate<String, Object> rest;
    private Producer producer;

    @Autowired
    public Rest(OrderRepository orderRepository, KafkaTemplate<String, Object> rest, Producer producer) {
        this.orderRepository = orderRepository;
        this.rest = rest;
        this.producer = producer;
        rest.setDefaultTopic(RestTopics.RESPONSE);
    }

    @KafkaHandler
    public void consumeOrderAdd(OrderAddPayload payload) {
        Order order = new Order(payload.getUserId());
        producer.emitOrderCreated(order);
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
    }

    @KafkaHandler
    public void consumerOderDelete(OrderDeletePayload payload) {
        try {
            Order order = orderRepository.findOrElseThrow(payload.getId());
            producer.emitOrderDeleted(order);
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeOrderGet(OrderGetPayload payload) {
        try {
            Order order = orderRepository.findOrElseThrow(payload.getId());
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeOrderItemAdd(OrderItemAddPayload payload) {
        try {
            Order order = orderRepository.findOrElseThrow(payload.getId());
            order.addItem(payload.getItemId());
            producer.emitOrderItemAdded(order);
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeOrderItemDelete(OrderItemDeletePayload payload) {
        try {
            Order order = orderRepository.findOrElseThrow(payload.getId());
            order.deleteItem(payload.getItemId());
            producer.emitOrderItemDeleted(order);
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void readyOrder(OrderReadyPayload payload) {
        try {
            Order order = orderRepository.findOrElseThrow(payload.getId());
            producer.emitOrderReady(order);
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
    }
}
