package nl.tudelft.wdm.group1.orders.events;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.common.payload.*;
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

    @Autowired
    public Rest(OrderRepository orderRepository, KafkaTemplate<String, Object> rest) {
        this.orderRepository = orderRepository;
        this.rest = rest;
        rest.setDefaultTopic(RestTopics.RESPONSE);
    }

    @KafkaHandler
    public void consumeOrderAdd(OrderAddPayload payload) {
        Order order = new Order(payload.getUserId());
        orderRepository.addOrReplace(order);
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
    }

    @KafkaHandler
    public void consumerOderDelete(OrderDeletePayload payload) {
        try {
            Order order = orderRepository.find(payload.getId());
            orderRepository.remove(payload.getId());
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeOrderGet(OrderGetPayload payload) {
        try {
            Order order = orderRepository.find(payload.getId());
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeOrderItemAdd(OrderItemAddPayload payload) {
        try {
            Order order = orderRepository.find(payload.getId());
            order.addItem(payload.getItemId());
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeOrderItemDelete(OrderItemDeletePayload payload) {
        try {
            Order order = orderRepository.find(payload.getId());
            order.deleteItem(payload.getItemId());
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), order));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }
}
