package nl.tudelft.wdm.group1.payments.web;

import nl.tudelft.wdm.group1.payments.PaymentRepository;
import nl.tudelft.wdm.group1.payments.ResourceNotFoundException;
import nl.tudelft.wdm.group1.payments.Payment;
import nl.tudelft.wdm.group1.payments.events.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    //POST /payments/{user_id}/{order_id}
    //subtracts the amount of the order from the users credit (returns failure if credit is not enough)
    @PostMapping("/{userId}/{orderId}")
    public Payment addPayment(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "orderId") UUID orderId) {
        if(!paymentRepository.containsPaymentOrderId(orderId)) {
            Payment payment = new Payment(userId, orderId);
            // TODO: subtracts the amount of the order from the user's credit (returns failure if credit is not enough)
            producer.emitPaymentCreated(order);
            return payment;
        } else {
            return null;
        }
    }

    //DELETE /payments/{user_id}/{order_id}
    //cancels payment made by a specific user for a specific order.
    @DeleteMapping("/{userId}/{orderId}")
    public Payment deletePayment(@PathVariable(value = "userId") UUID userId, @PathVariable(value = "orderId") UUID orderId) throws ResourceNotFoundException {
        Payment payment = paymentRepository.findByOrderId(orderId);
        // TODO: adds the amount of the order to the user's credit
        producer.emitPaymentDeleted(order);
        return payment;
    }

    //GET /payments/{order_id}
    //returns the status of the payment (paid or not)
    @GetMapping("/{orderId}")
    public Payment getPayment(@PathVariable(value = "orderId") UUID orderId) throws ResourceNotFoundException {
        return paymentRepository.findByOrderId(orderId);
    }
}
