package nl.tudelft.wdm.group1.orders;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class OrderRepository {
    private Map<UUID, Order> orders = new HashMap<>();

    public Order add(Order order) {
        orders.putIfAbsent(order.getId(), order);

        return order;
    }

    public Order addOrReplace(Order order) {
        orders.put(order.getId(), order);

        return order;
    }

    public Order find(UUID id) throws ResourceNotFoundException {
        if (!orders.containsKey(id)) {
            throw new ResourceNotFoundException("Order with ID " + id + " cannot be found.");
        }
        return orders.get(id);
    }

    public void remove(UUID id) throws ResourceNotFoundException {
        if (!orders.containsKey(id)) {
            throw new ResourceNotFoundException("Order with ID " + id + " cannot be found.");
        }
        orders.remove(id);
    }
}
