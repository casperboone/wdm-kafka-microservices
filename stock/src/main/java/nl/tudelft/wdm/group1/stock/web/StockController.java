package nl.tudelft.wdm.group1.stock.web;

import nl.tudelft.wdm.group1.stock.InsufficientStockException;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
import nl.tudelft.wdm.group1.stock.ResourceNotFoundException;
import nl.tudelft.wdm.group1.stock.StockItem;
import nl.tudelft.wdm.group1.stock.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public StockItem addStockItem(@RequestParam("stock") int stock) {
        StockItem stockItem = new StockItem(stock);

        producer.send(stockItem);

        return stockItem;
    }

    @GetMapping("/{id}")
    public StockItem getStockItem(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        return stockItemRepository.find(id);
    }

    @PostMapping("/{id}/subtract/{amount}")
    public StockItem substractStockItemAmount(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) throws ResourceNotFoundException, InsufficientStockException {
        StockItem item = stockItemRepository.find(id);
        item.subtractStock(amount);
        producer.send(item);

        return item;
    }

    @PostMapping("{id}/add/{amount}")
    public StockItem addStockItemAmount(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) throws ResourceNotFoundException {
        StockItem item = stockItemRepository.find(id);
        item.addStock(amount);
        producer.send(item);

        return item;
    }

}
