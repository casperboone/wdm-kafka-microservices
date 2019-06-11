package nl.tudelft.wdm.group1.rest.web;

import nl.tudelft.wdm.group1.common.model.Payment;
import nl.tudelft.wdm.group1.common.payload.PaymentAddPayload;
import nl.tudelft.wdm.group1.common.payload.PaymentDeletePayload;
import nl.tudelft.wdm.group1.common.payload.PaymentGetPayload;
import nl.tudelft.wdm.group1.common.topic.RestTopics;
import nl.tudelft.wdm.group1.rest.events.KafkaInteraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController {

    private final KafkaInteraction<Payment> kafka;

    @Autowired
    PaymentController(KafkaInteraction<Payment> kafka) {
        this.kafka = kafka;
    }

    /*
     * POST /payments/{user_id}/{order_id}
     * subtracts the amount of the order from the users credit (returns failure if credit is not enough)
     */
    @PostMapping("/{userId}/{orderId}/{amount}")
    public CompletableFuture<Payment> addPayment(
            @PathVariable(value = "userId") UUID userId,
            @PathVariable(value = "orderId") UUID orderId,
            @PathVariable(value = "amount") int amount
    ) {
        return kafka.performAction(RestTopics.PAYMENT_REQUEST, new PaymentAddPayload(userId, orderId, amount));
    }

    /*
     * DELETE /payments/{user_id}/{order_id}
     * cancels payment made by a specific user for a specific order.
     */
    @DeleteMapping("/{userId}/{orderId}")
    public CompletableFuture<Payment> deletePayment(
            @PathVariable(value = "userId") UUID userId,
            @PathVariable(value = "orderId") UUID orderId
    ) {
        return kafka.performAction(RestTopics.PAYMENT_REQUEST, new PaymentDeletePayload(userId, orderId));
    }

    /*
     * GET /payments/{order_id}
     * returns the status of the payment (paid or not)
     */
    @GetMapping("/{orderId}")
    public CompletableFuture<Payment> getPayment(@PathVariable(value = "orderId") UUID orderId) {
        return kafka.performAction(RestTopics.PAYMENT_REQUEST, new PaymentGetPayload(orderId));
    }
}
