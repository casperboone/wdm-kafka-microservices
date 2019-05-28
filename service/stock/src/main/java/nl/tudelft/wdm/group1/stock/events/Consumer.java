package nl.tudelft.wdm.group1.stock.events;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    public void consume(final StockItem stockItem) {
        logger.info(String.format("#### -> Consumed message -> %s", stockItem));

        stockItemRepository.addOrReplace(stockItem);
    }

    @KafkaListener(topics = {OrdersTopics.ORDER_CHECKED_OUT})
    public void consumeOrderCheckedOut(final Order order)
            throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        logger.info(String.format("#### -> Consumed message -> %s", order));

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();

        // lock all stock items
        readLock.lock();
        try {
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
        } finally {
            readLock.unlock();
        }

        // all stock items available, proceed and emit a successful event
        int totalPrice = 0;

        writeLock.lock();
        try {
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
        } finally {
            writeLock.unlock();
        }

        order.setPrice(totalPrice);
        producer.emitStockItemsSubtractedForOrder(order);
    }

}
