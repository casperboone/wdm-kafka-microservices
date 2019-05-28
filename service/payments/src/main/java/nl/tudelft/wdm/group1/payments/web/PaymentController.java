package nl.tudelft.wdm.group1.payments.web;

import nl.tudelft.wdm.group1.common.Payment;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
import nl.tudelft.wdm.group1.payments.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController {

    private final Producer producer;
    private final PaymentRepository paymentRepository;

    @Autowired
    PaymentController(Producer producer, PaymentRepository paymentRepository) {
        this.producer = producer;
        this.paymentRepository = paymentRepository;
    }

    /*
     * POST /payments/{user_id}/{order_id}
     * subtracts the amount of the order from the users credit (returns failure if credit is not enough)
     */
    @PostMapping("/{userId}/{orderId}/{amount}")
    public Payment addPayment(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "orderId") UUID orderId, @PathVariable(value = "amount") int amount) {
        if(!paymentRepository.existsById(orderId)) {
            Payment payment = new Payment(userId, orderId, amount);
            producer.emitPaymentCreated(payment);
            return payment;
        } else {
            return null;
        }
    }

    /*
     * DELETE /payments/{user_id}/{order_id}
     * cancels payment made by a specific user for a specific order.
     */
    @DeleteMapping("/{userId}/{orderId}")
    public Payment deletePayment(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "orderId") UUID orderId) throws ResourceNotFoundException {
        Payment payment = paymentRepository.findOrElseThrow(orderId);
        producer.emitPaymentDeleted(payment);
        return payment;
    }

    /*
     * GET /payments/{order_id}
     * returns the status of the payment (paid or not)
     */
    @GetMapping("/{orderId}")
    public Payment getPayment(@PathVariable(value = "orderId") UUID orderId) throws ResourceNotFoundException {
        return paymentRepository.findOrElseThrow(orderId);
    }
}
