package nl.tudelft.wdm.group1.stock.events;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.common.payload.*;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(topics = RestTopics.REQUEST)
public class Rest {

    private final StockItemRepository stockItemRepository;
    private KafkaTemplate<String, Object> rest;

    @Autowired
    public Rest(StockItemRepository stockItemRepository, KafkaTemplate<String, Object> rest) {
        this.stockItemRepository = stockItemRepository;
        this.rest = rest;
        rest.setDefaultTopic(RestTopics.RESPONSE);
    }

    @KafkaHandler
    public void consumeStockItemCreate(StockItemCreatePayload payload) {
        StockItem stockItem = new StockItem(payload.getStock(), payload.getName(), payload.getPrice());
        stockItemRepository.addOrReplace(stockItem);
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), stockItem));
    }

    @KafkaHandler
    public void consumeStockItemGet(StockItemGetPayload payload) throws ResourceNotFoundException {
        StockItem stockItem = stockItemRepository.find(payload.getId());
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), stockItem));
    }

    @KafkaHandler
    public void consumeStockItemAddAmount(StockItemAddAmountPayload payload) {
        try {
            StockItem stockItem = stockItemRepository.find(payload.getId());
            stockItem.addStock(payload.getAmount());

            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), stockItem));
        } catch (ResourceNotFoundException | InvalidStockChangeException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeStockItemSubtractAmount(StockItemSubtractAmountPayload payload) {
        try {
            StockItem stockItem = stockItemRepository.find(payload.getId());
            stockItem.subtractStock(payload.getAmount());

            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), stockItem));
        } catch (ResourceNotFoundException | InsufficientStockException | InvalidStockChangeException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
    }
}

