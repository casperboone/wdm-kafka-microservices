package nl.tudelft.wdm.group1.stock;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class StockItemRepository {
    private Map<UUID, StockItem> stockItems = new HashMap<>();

    public StockItem add(StockItem stockItem) {
        stockItems.putIfAbsent(stockItem.getId(), stockItem);

        return stockItem;
    }

    public StockItem addOrReplace(StockItem stockItem) {
        stockItems.put(stockItem.getId(), stockItem);

        return stockItem;
    }

    public StockItem find(UUID id) throws ResourceNotFoundException {
        if (!stockItems.containsKey(id)) {
            throw new ResourceNotFoundException("Stock item with ID " + id + " cannot be found.");
        }
        return stockItems.get(id);
    }

    public void remove(UUID id) throws ResourceNotFoundException {
        if (!stockItems.containsKey(id)) {
            throw new ResourceNotFoundException("Stock item with ID " + id + " cannot be found.");
        }
        stockItems.remove(id);
    }
}
