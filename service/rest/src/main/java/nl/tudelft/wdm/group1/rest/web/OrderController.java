package nl.tudelft.wdm.group1.rest.web;

import nl.tudelft.wdm.group1.common.Order;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.payload.*;
import nl.tudelft.wdm.group1.rest.events.KafkaInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

    private final KafkaInteraction<Order> kafka;

    @Autowired
    OrderController(KafkaInteraction<Order> kafka) {
        this.kafka = kafka;
    }

    @PostMapping("/{userId}")
    public CompletableFuture<Order> addOrder(@PathVariable(value = "userId") UUID userId) {
        return kafka.performAction(new OrderAddPayload(userId));
    }

    @GetMapping("/{id}")
    public CompletableFuture<Order> getOrder(@PathVariable(value = "id") UUID id) {
        return kafka.performAction(new OrderGetPayload(id));
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<Order> deleteOrder(@PathVariable(value = "id") UUID id) {
        return kafka.performAction(new OrderDeletePayload(id));
    }

    @PostMapping("/{id}/items")
    public CompletableFuture<Order> addOrderItem(@PathVariable(value = "id") UUID id, @RequestParam("itemId") UUID itemId) {
        return kafka.performAction(new OrderItemAddPayload(id, itemId));
    }

    @DeleteMapping("/{id}/items")
    public CompletableFuture<Order> deleteOrderItem(@PathVariable(value = "id") UUID id, @RequestParam("itemId") UUID itemId) {
        return kafka.performAction(new OrderItemDeletePayload(id, itemId));
    }

    @PostMapping("/{id}/checkout")
    public CompletableFuture<Order> checkoutOrder(@PathVariable(value = "id") UUID id) {
        return kafka.performAction(new OrderCheckoutPayload(id));
    }
}
