package nl.tudelft.wdm.group1.orders.web;

import nl.tudelft.wdm.group1.orders.OrderRepository;
import nl.tudelft.wdm.group1.orders.ResourceNotFoundException;
import nl.tudelft.wdm.group1.orders.Order;
import nl.tudelft.wdm.group1.orders.OrderRepository;
import nl.tudelft.wdm.group1.orders.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public Order addOrder() {
        Order order = new Order();

        producer.send(order);

        return order;
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        return orderRepository.find(id);
    }
}
