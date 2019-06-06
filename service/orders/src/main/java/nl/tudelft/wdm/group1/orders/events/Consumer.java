package nl.tudelft.wdm.group1.orders.events;

import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.Order;
import nl.tudelft.wdm.group1.common.model.OrderStatus;
import nl.tudelft.wdm.group1.common.model.Payment;
import nl.tudelft.wdm.group1.common.topic.OrdersTopics;
import nl.tudelft.wdm.group1.common.topic.PaymentsTopics;
import nl.tudelft.wdm.group1.orders.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    private final OrderRepository orderRepository;
    private final Producer producer;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public Consumer(OrderRepository orderRepository, Producer producer) {
        this.orderRepository = orderRepository;
        this.producer = producer;
    }

    @KafkaListener(topics = {
            OrdersTopics.ORDER_CREATED,
            OrdersTopics.ORDER_ITEM_ADDED,
            OrdersTopics.ORDER_ITEM_DELETED,
            OrdersTopics.ORDER_CHECKED_OUT
    })
    public void consume(Order order) {
        logger.info("Consuming [{},{},{},{}] -> {}",
                OrdersTopics.ORDER_CREATED, OrdersTopics.ORDER_ITEM_ADDED,
                OrdersTopics.ORDER_ITEM_DELETED, OrdersTopics.ORDER_CHECKED_OUT, order);

        orderRepository.save(order);
    }

    @KafkaListener(topics = OrdersTopics.ORDER_DELETED)
    public void consumeOrderDeleted(Order order) {
        logger.info("Consuming [{}] -> {}", OrdersTopics.ORDER_DELETED, order);

        orderRepository.deleteById(order.getId());
    }

    @KafkaListener(topics = {OrdersTopics.ORDER_PROCESSED_IN_STOCK_SUCCESSFUL})
    public void consumeOrderProcessedInStockSuccessful(Order order) {
        logger.info("Consuming [{}] -> {}", OrdersTopics.ORDER_PROCESSED_IN_STOCK_SUCCESSFUL, order);

        order.setProcessedInStock(true);
        orderRepository.save(order);

        producer.emitOrderCheckedOut(order);
    }

    @KafkaListener(topics = {OrdersTopics.ORDER_PROCESSED_IN_STOCK_FAILED})
    public void consumeOrderProcessedInStockFailed(Order order) {
        logger.info("Consuming [{}] -> {}", OrdersTopics.ORDER_PROCESSED_IN_STOCK_FAILED, order);

        order.setStatus(OrderStatus.FAILED_DUE_TO_LACK_OF_STOCK);
        orderRepository.save(order);

        producer.emitOrderCancelled(order);

        // TODO notify user
    }

    @KafkaListener(topics = {PaymentsTopics.PAYMENT_SUCCESSFUL})
    public void consumePaymentSuccessful(Payment payment) throws ResourceNotFoundException {
        logger.info("Consuming [{}] -> {}", PaymentsTopics.PAYMENT_SUCCESSFUL, payment);

        Order order = orderRepository.findOrElseThrow(payment.getOrderId());

        order.setPaid(true);
        order.setStatus(OrderStatus.SUCCEEDED);
        orderRepository.save(order);

        // TODO notify user
    }

    @KafkaListener(topics = {PaymentsTopics.PAYMENT_FAILED})
    public void consumePaymentFailed(Payment payment) throws ResourceNotFoundException {
        logger.info("Consuming [{}] -> {}", PaymentsTopics.PAYMENT_FAILED, payment);

        Order order = orderRepository.findOrElseThrow(payment.getOrderId());

        order.setStatus(OrderStatus.FAILED_DUE_TO_LACK_OF_PAYMENT);
        orderRepository.save(order);

        producer.emitOrderCancelled(order);

        // TODO notify user
    }
}
