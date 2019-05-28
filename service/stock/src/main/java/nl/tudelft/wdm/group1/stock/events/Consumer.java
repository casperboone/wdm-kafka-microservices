package nl.tudelft.wdm.group1.stock.events;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.common.OrdersTopics;
import nl.tudelft.wdm.group1.stock.*;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.UUID;

@Service
public class Consumer {
    private final StockItemRepository stockItemRepository;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private Producer producer;

    public Consumer(final StockItemRepository stockItemRepository, final Producer producer) {
        this.stockItemRepository = stockItemRepository;
        this.producer = producer;
    }

    @KafkaListener(topics = {StockTopics.STOCK_ITEM_CREATED, StockTopics.STOCK_ADDED, StockTopics.STOCK_SUBTRACTED})
    public void consumeStockItemChange(final StockItem stockItem) {
        logger.info(String.format("#### -> Consumed message -> %s", stockItem));

        stockItemRepository.addOrReplace(stockItem);
    }

    @KafkaListener(topics = {OrdersTopics.ORDER_CHECKED_OUT})
    public void consumeOrderCheckedOut(final Order order)
            throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        logger.info(String.format("#### -> Consumed message -> %s", order));

        // TODO: lock the stock to prevent multiple checks on same stock
        // check availability of all StockItems
        for (UUID stockItemId : order.getItemIds()) {
            StockItem stockItem;
            try {
                stockItem = stockItemRepository.find(stockItemId);
            } catch (ResourceNotFoundException e) {
                producer.emitStockItemsSubtractForOrderFailed(order);
                return;
            }

            if (stockItem.getStock() < 1) { // amount per order item is 1
                producer.emitStockItemsSubtractForOrderFailed(order);
                return;
            }
        }

        // all stocks available, proceed and emit a successful event
        int totalPrice = 0;

        for (UUID stockItemId : order.getItemIds()) {
            StockItem stockItem;
            try {
                stockItem = stockItemRepository.find(stockItemId);
            } catch (ResourceNotFoundException e) {
                // we assume this should not cause any exception
                throw e;
            }

            try {
                stockItem.subtractStock(1);
            } catch (InsufficientStockException | InvalidStockChangeException e) {
                // we assume the subtraction should not cause any exception
                throw e;
            }

            totalPrice += stockItem.getPrice();
        }

        order.setPrice(totalPrice);
        producer.emitStockItemsSubtractedForOrder(order);
    }

    @KafkaListener(topics = {OrdersTopics.ORDER_CANCELLED})
    public void consumeOrderCancelled(Order order) {
        logger.info(String.format("#### -> Consumed message -> %s", order));
        // Only perform action when the order was cancelled due to lack of payment
        if(order.getStatus() == OrderStatus.FAILEDDUETOLACKOFPAYMENT){
            // TODO: lock the stock while adding
            for (UUID stockItemId : order.getItemIds()) {
                try {
                    StockItem stockItem = stockItemRepository.find(stockItemId);
                    stockItem.addStock(1);
                } catch (ResourceNotFoundException | InvalidStockChangeException e) {
                    // we assume this should not cause any exception
                    e.printStackTrace();
                }
            }
        }
    }

}
