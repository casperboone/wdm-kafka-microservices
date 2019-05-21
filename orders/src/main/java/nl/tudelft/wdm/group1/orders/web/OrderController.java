package nl.tudelft.wdm.group1.orders.web;

import nl.tudelft.wdm.group1.common.Order;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.orders.OrderRepository;
import nl.tudelft.wdm.group1.orders.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

    private final Producer producer;
    private final OrderRepository orderRepository;

    @Autowired
    OrderController(Producer producer, OrderRepository orderRepository) {
        this.producer = producer;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/{userId}")
    public Order addOrder(@PathVariable(value = "userId") UUID userId) {
        Order order = new Order(userId);

        producer.emitOrderCreated(order);

        return order;
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        return orderRepository.find(id);
    }

    @DeleteMapping("/{id}")
    public Order deleteOrder(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        Order order = orderRepository.find(id);

        producer.emitOrderDeleted(order);

        return order;
    }

    @PostMapping("/{id}/items")
    public Order addOrderItem(@PathVariable(value = "id") UUID id, @RequestParam("itemId") UUID itemId) throws ResourceNotFoundException {
        Order order = orderRepository.find(id);
        order.addItem(itemId);

        producer.emitOrderItemAdded(order);

        return order;
    }

    @DeleteMapping("/{id}/items")
    public Order deleteOrderItem(@PathVariable(value = "id") UUID id, @RequestParam("itemId") UUID itemId) throws ResourceNotFoundException {
        Order order = orderRepository.find(id);
        order.deleteItem(itemId);

        producer.emitOrderItemDeleted(order);

        return order;
    }

    @PostMapping("/{id}/checkout")
    public Order checkoutOrder(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        Order order = orderRepository.find(id);

        producer.emitOrderCheckedOut(order);

        return order;
    }
}
