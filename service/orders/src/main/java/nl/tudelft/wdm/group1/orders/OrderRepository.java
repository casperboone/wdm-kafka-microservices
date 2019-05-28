package nl.tudelft.wdm.group1.orders;

import nl.tudelft.wdm.group1.common.Order;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface OrderRepository extends CrudRepository<Order, UUID> {
    default Order findOrElseThrow(UUID id) throws ResourceNotFoundException {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + id + " cannot be found."));
    }
}
