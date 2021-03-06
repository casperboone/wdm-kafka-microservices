package nl.tudelft.wdm.group1.stock.events;

import nl.tudelft.wdm.group1.common.model.Order;
import nl.tudelft.wdm.group1.common.model.StockItem;
import nl.tudelft.wdm.group1.common.topic.OrdersTopics;
import nl.tudelft.wdm.group1.common.topic.StockTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, StockItem> kafkaTemplateForStock;

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplateForOrder;

    public void emitStockItemAdded(final StockItem stockItem) {
        logger.info("Producing [{}] -> {}", StockTopics.STOCK_ADDED, stockItem);
        this.kafkaTemplateForStock.send(StockTopics.STOCK_ADDED, stockItem);
    }

    public void emitStockItemSubtracted(final StockItem stockItem) {
        logger.info("Producing [{}] -> {}", StockTopics.STOCK_SUBTRACTED, stockItem);
        this.kafkaTemplateForStock.send(StockTopics.STOCK_SUBTRACTED, stockItem);
    }

    public void emitStockItemsSubtractedForOrder(final Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_PROCESSED_IN_STOCK_SUCCESSFUL, order);
        this.kafkaTemplateForOrder.send(OrdersTopics.ORDER_PROCESSED_IN_STOCK_SUCCESSFUL, order);
    }

    public void emitStockItemsSubtractForOrderFailed(final Order order) {
        logger.info("Producing [{}] -> {}", OrdersTopics.ORDER_PROCESSED_IN_STOCK_FAILED, order);
        this.kafkaTemplateForOrder.send(OrdersTopics.ORDER_PROCESSED_IN_STOCK_FAILED, order);
    }

}
