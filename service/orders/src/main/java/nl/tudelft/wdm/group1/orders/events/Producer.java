package nl.tudelft.wdm.group1.orders.events;

import nl.tudelft.wdm.group1.common.Order;
import nl.tudelft.wdm.group1.common.OrdersTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    public void emitOrderCreated(Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_CREATED, order);
        this.kafkaTemplate.send(OrdersTopics.ORDER_CREATED, order);
    }

    public void emitOrderDeleted(Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_DELETED, order);
        this.kafkaTemplate.send(OrdersTopics.ORDER_DELETED, order);
    }

    public void emitOrderItemAdded(Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_ITEM_ADDED, order);
        this.kafkaTemplate.send(OrdersTopics.ORDER_ITEM_ADDED, order);
    }

    public void emitOrderItemDeleted(Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_ITEM_DELETED, order);
        this.kafkaTemplate.send(OrdersTopics.ORDER_ITEM_DELETED, order);
    }

    public void emitOrderCheckedOut(Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_CHECKED_OUT, order);
        this.kafkaTemplate.send(OrdersTopics.ORDER_CHECKED_OUT, order);
    }

    public void emitOrderCancelled(Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_CANCELLED, order);
        this.kafkaTemplate.send(OrdersTopics.ORDER_CANCELLED, order);
    }

    public void emitOrderReady(Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_READY, order);
        this.kafkaTemplate.send(OrdersTopics.ORDER_READY, order);
    }
}
