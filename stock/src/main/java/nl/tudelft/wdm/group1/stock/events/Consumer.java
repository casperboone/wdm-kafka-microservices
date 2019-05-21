package nl.tudelft.wdm.group1.stock.events;

import nl.tudelft.wdm.group1.common.StockTopics;
import nl.tudelft.wdm.group1.stock.StockItem;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    private final StockItemRepository stockItemRepository;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public Consumer(StockItemRepository stockItemRepository) {
        this.stockItemRepository = stockItemRepository;
    }

    @KafkaListener(topics = {StockTopics.STOCK_ITEM_CREATED, StockTopics.STOCK_ADDED, StockTopics.STOCK_SUBTRACTED})
    public void consume(final StockItem stockItem) {
        logger.info(String.format("#### -> Consumed message -> %s", stockItem));

        stockItemRepository.addOrReplace(stockItem);
    }
}
