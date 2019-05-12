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

    @PostMapping
    public Payment addPayment() {
        Payment payment = new Payment();

        producer.send(payment);

        return payment;
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable(value = "id") UUID id) throws ResourceNotFoundException {
        return paymentRepository.find(id);
    }
}
