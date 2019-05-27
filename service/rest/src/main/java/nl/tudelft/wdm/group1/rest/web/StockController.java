package nl.tudelft.wdm.group1.rest.web;

import nl.tudelft.wdm.group1.common.StockItem;
import nl.tudelft.wdm.group1.common.payload.StockItemAddAmountPayload;
import nl.tudelft.wdm.group1.common.payload.StockItemCreatePayload;
import nl.tudelft.wdm.group1.common.payload.StockItemGetPayload;
import nl.tudelft.wdm.group1.common.payload.StockItemSubtractAmountPayload;
import nl.tudelft.wdm.group1.rest.events.KafkaInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/stock")
public class StockController {
    private final KafkaInteraction<StockItem> kafka;

    @Autowired
    StockController(KafkaInteraction<StockItem> kafka) {
        this.kafka = kafka;
    }

    @PostMapping
    public CompletableFuture<StockItem> addStockItem(
            @RequestParam("stock") int stock,
            @RequestParam("name") String name,
            @RequestParam("price") int price
    ) {
        return kafka.performAction(new StockItemCreatePayload(stock, name, price));
    }

    @GetMapping("/{id}")
    public CompletableFuture<StockItem> getStockItem(@PathVariable(value = "id") UUID id) {
        return kafka.performAction(new StockItemGetPayload(id));
    }

    @PostMapping("/{id}/subtract/{amount}")
    public CompletableFuture<StockItem> subtractStockItemAmount(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) {
        return kafka.performAction(new StockItemSubtractAmountPayload(id, amount));
    }

    @PostMapping("{id}/add/{amount}")
    public CompletableFuture<StockItem> addStockItemAmount(
            @PathVariable(value = "id") UUID id,
            @PathVariable(value = "amount") int amount
    ) {
        return kafka.performAction(new StockItemAddAmountPayload(id, amount));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleException(Throwable ex) {
        return ex.getMessage();
    }
}
