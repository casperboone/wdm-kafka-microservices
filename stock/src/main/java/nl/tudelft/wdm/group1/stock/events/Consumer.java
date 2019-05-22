package nl.tudelft.wdm.group1.stock.events;

import nl.tudelft.wdm.group1.orders.Order;
import nl.tudelft.wdm.group1.stock.*;
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

    @KafkaListener(topics = {"stockItemCreated", "stockAdded", "stockSubtracted"})
    public void consume(final StockItem stockItem) {
        logger.info(String.format("#### -> Consumed message -> %s", stockItem));

        stockItemRepository.addOrReplace(stockItem);
    }

    @KafkaListener(topics = {"orderCheckedOut"})
    public void consumeOrderCheckedOut(final Order order)
            throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        logger.info(String.format("#### -> Consumed message -> %s", order));

        // TODO: lock the stock to prevent multiple checks on same stock
        // check availability of all StockItems
        Iterator<UUID> stockItemIdIterator = order.getItemIds().iterator();

        while (stockItemIdIterator.hasNext()) {
            StockItem stockItem;
            try {
                stockItem = stockItemRepository.find(stockItemIdIterator.next());
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
        stockItemIdIterator = order.getItemIds().iterator();

        while(stockItemIdIterator.hasNext()) {
            StockItem stockItem;
            try {
                stockItem = stockItemRepository.find(stockItemIdIterator.next());
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

            totalPrice += stockItem.getPrice() * 1;
        }

        order.setPrice(totalPrice);
        producer.emitStockItemsSubtractedForOrder(order);
    }

}
