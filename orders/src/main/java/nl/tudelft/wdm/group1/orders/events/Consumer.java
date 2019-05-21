package nl.tudelft.wdm.group1.orders.events;

import nl.tudelft.wdm.group1.common.Order;
import nl.tudelft.wdm.group1.common.OrdersTopics;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.orders.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    private final OrderRepository orderRepository;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public Consumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @KafkaListener(topics = {
            OrdersTopics.ORDER_CREATED,
            OrdersTopics.ORDER_ITEM_ADDED,
            OrdersTopics.ORDER_ITEM_DELETED,
            OrdersTopics.ORDER_CHECKED_OUT
    })
    public void consume(Order order) {
        logger.info(String.format("#### -> Consumed message -> %s", order));

        orderRepository.addOrReplace(order);
    }

    @KafkaListener(topics = OrdersTopics.ORDER_DELETED)
    public void consumeOrderDeleted(Order order) throws ResourceNotFoundException {
        logger.info(String.format("#### -> Consumed message -> %s", order));

        orderRepository.remove(order.getId());
    }
}
