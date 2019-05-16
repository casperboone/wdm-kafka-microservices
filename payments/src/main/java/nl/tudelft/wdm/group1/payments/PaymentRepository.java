package nl.tudelft.wdm.group1.payments;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class PaymentRepository {
    private Map<UUID, Payment> payments = new HashMap<>();
    private Map<UUID, Payment> paymentsByOrderId = new HashMap<>();

    public Payment add(Payment payment) {
        payments.putIfAbsent(payment.getId(), payment);
        paymentsByOrderId.putIfAbsent(payment.getOrderId(), payment);
        return payment;
    }

    public Payment addOrReplace(Payment payment) {
        payments.put(payment.getId(), payment);

        return payment;
    }

    public Payment find(UUID id) throws ResourceNotFoundException {
        if (!payments.containsKey(id)) {
            throw new ResourceNotFoundException("Payment with ID " + id + " cannot be found.");
        }
        return payments.get(id);
    }

    public Payment findByOrderId(UUID orderId) throws ResourceNotFoundException {
        if (!paymentsByOrderId.containsKey(orderId)) {
            throw new ResourceNotFoundException("Payment with OrderID " + orderId + " cannot be found.");
        }
        return paymentsByOrderId.get(orderId);
    }

    public boolean containsPaymentOrderId(UUID orderId) {
        return paymentsByOrderId.containsKey(orderId);
    }

    public void remove(UUID id) throws ResourceNotFoundException {
        if (!payments.containsKey(id)) {
            throw new ResourceNotFoundException("Payment with ID " + id + " cannot be found.");
        }
        payments.remove(id);
    }
}
