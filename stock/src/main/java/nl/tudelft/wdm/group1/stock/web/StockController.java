package nl.tudelft.wdm.group1.stock.web;

import nl.tudelft.wdm.group1.stock.StockItemRepository;
import nl.tudelft.wdm.group1.stock.ResourceNotFoundException;
import nl.tudelft.wdm.group1.stock.StockItem;
import nl.tudelft.wdm.group1.stock.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/stock")
public class StockController {

    private final Producer producer;
    private final StockItemRepository stockItemRepository;

    @Autowired
    StockController(Producer producer, StockItemRepository stockItemRepository) {
        this.producer = producer;
        this.stockItemRepository = stockItemRepository;
    }

    @PostMapping
    public StockItem addStockItem() {
        StockItem stockItem = new StockItem();

        producer.send(stockItem);

        return stockItem;
    }

    @GetMapping("/{id}")
    public StockItem getStockItem(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        return stockItemRepository.find(id);
    }
}
