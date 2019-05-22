package nl.tudelft.wdm.group1.stock.events;

import nl.tudelft.wdm.group1.orders.Order;
import nl.tudelft.wdm.group1.stock.StockItem;
import nl.tudelft.wdm.group1.common.StockItem;
import nl.tudelft.wdm.group1.common.StockTopics;
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

    public void emitStockItemCreated(final StockItem stockItem) {
        logger.info(String.format("#### -> Producing message -> %s", stockItem));
        this.kafkaTemplateForStock.send("stockItemCreated", stockItem);
        this.kafkaTemplate.send(StockTopics.STOCK_ITEM_CREATED, stockItem);
    }

    public void emitStockItemAdded(final StockItem stockItem) {
        logger.info(String.format("#### -> Producing message -> %s", stockItem));
        this.kafkaTemplateForStock.send("stockAdded", stockItem);
        this.kafkaTemplate.send(StockTopics.STOCK_ADDED, stockItem);
    }

    public void emitStockItemSubtracted(final StockItem stockItem) {
        logger.info(String.format("#### -> Producing message -> %s", stockItem));
        this.kafkaTemplateForStock.send("stockSubtracted", stockItem);
    }

    public void emitStockItemsSubtractedForOrder(final Order order) {
        logger.info(String.format("#### -> Producing message -> %s", order));
        this.kafkaTemplateForOrder.send("orderProcessedInStockSuccessful", order);
    }

    public void emitStockItemsSubtractForOrderFailed(final Order order) {
        logger.info(String.format("#### -> Producing message -> %s", order));
        this.kafkaTemplateForOrder.send("orderProcessedInStockFailed", order);
        this.kafkaTemplate.send(StockTopics.STOCK_SUBTRACTED, stockItem);
    }

}
