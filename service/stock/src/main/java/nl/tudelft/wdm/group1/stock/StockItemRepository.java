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
