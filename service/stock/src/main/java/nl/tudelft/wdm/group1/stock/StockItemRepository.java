package nl.tudelft.wdm.group1.stock;

import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.StockItem;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface StockItemRepository extends CrudRepository<StockItem, UUID> {
    default StockItem findOrElseThrow(UUID id) throws ResourceNotFoundException {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Stock item with ID " + id + " cannot be found."));
    }
}

//@Repository
//public class StockItemRepository {
//    private Map<UUID, StockItem> stockItems = new HashMap<>();
//
//    public StockItem add(StockItem stockItem) {
//        stockItems.putIfAbsent(stockItem.getId(), stockItem);
//
//        return stockItem;
//    }
//
//    public StockItem addOrReplace(StockItem stockItem) {
//        stockItems.put(stockItem.getId(), stockItem);
//
//        return stockItem;
//    }
//
//    public boolean contains(UUID id) {
//        return stockItems.containsKey(id);
//    }
//
//    public StockItem find(UUID id) throws ResourceNotFoundException {
//        if (!stockItems.containsKey(id)) {
//            throw new ResourceNotFoundException("Stock item with ID " + id + " cannot be found.");
//        }
//        return stockItems.get(id);
//    }
//
//    public void remove(UUID id) throws ResourceNotFoundException {
//        if (!stockItems.containsKey(id)) {
//            throw new ResourceNotFoundException("Stock item with ID " + id + " cannot be found.");
//        }
//        stockItems.remove(id);
//    }
//}
