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
    private Producer producer;

    @Autowired
    public Rest(StockItemRepository stockItemRepository, KafkaTemplate<String, Object> rest, Producer producer) {
        this.stockItemRepository = stockItemRepository;
        this.rest = rest;
        this.producer = producer;
        rest.setDefaultTopic(RestTopics.RESPONSE);
    }

    @KafkaHandler
    public void consumeStockItemCreate(StockItemCreatePayload payload) {
        StockItem stockItem = new StockItem(payload.getStock(), payload.getName(), payload.getPrice());
        producer.emitStockItemAdded(stockItem);
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), stockItem));
    }

    @KafkaHandler
    public void consumeStockItemGet(StockItemGetPayload payload) throws ResourceNotFoundException {
        StockItem stockItem = stockItemRepository.findOrElseThrow(payload.getId());
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), stockItem));
    }

    @KafkaHandler
    public void consumeStockItemAddAmount(StockItemAddAmountPayload payload) {
        try {
            StockItem stockItem = stockItemRepository.findOrElseThrow(payload.getId());
            stockItem.addStock(payload.getAmount());
            producer.emitStockItemAdded(stockItem);

            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), stockItem));
        } catch (ResourceNotFoundException | InvalidStockChangeException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeStockItemSubtractAmount(StockItemSubtractAmountPayload payload) {
        try {
            StockItem stockItem = stockItemRepository.findOrElseThrow(payload.getId());
            stockItem.subtractStock(payload.getAmount());
            producer.emitStockItemSubtracted(stockItem);

            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), stockItem));
        } catch (ResourceNotFoundException | InsufficientStockException | InvalidStockChangeException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
    }
}

