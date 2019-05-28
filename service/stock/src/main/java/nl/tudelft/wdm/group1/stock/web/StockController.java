package nl.tudelft.wdm.group1.stock.web;

import nl.tudelft.wdm.group1.common.InsufficientStockException;
import nl.tudelft.wdm.group1.common.InvalidStockChangeException;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.StockItem;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
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
    public StockItem addStockItem(
            @RequestParam("stock") int stock,
            @RequestParam("name") String name,
            @RequestParam("price") int price
    ) {
        StockItem stockItem = new StockItem(stock, name, price);
        producer.emitStockItemCreated(stockItem);

        return stockItem;
    }

    @GetMapping("/{id}")
    public StockItem getStockItem(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        return stockItemRepository.findOrElseThrow(id);
    }

    @PostMapping("/{id}/subtract/{amount}")
    public StockItem substractStockItemAmount(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        StockItem item = stockItemRepository.findOrElseThrow(id);
        item.subtractStock(amount);
        producer.emitStockItemSubtracted(item);

        return item;
    }

    @PostMapping("{id}/add/{amount}")
    public StockItem addStockItemAmount(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) throws ResourceNotFoundException, InvalidStockChangeException {
        StockItem item = stockItemRepository.findOrElseThrow(id);
        item.addStock(amount);
        producer.emitStockItemAdded(item);

        return item;
    }

}
