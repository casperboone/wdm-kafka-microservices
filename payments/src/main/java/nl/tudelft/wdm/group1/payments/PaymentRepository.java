package nl.tudelft.wdm.group1.payments;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class PaymentRepository {
    private Map<UUID, Payment> payments = new HashMap<>();

    public Payment add(Payment payment) {
        payments.putIfAbsent(payment.getOrderId(), payment);
        return payment;
    }

    public Payment addOrReplace(Payment payment) {
        payments.put(payment.getOrderId(), payment);
        return payment;
    }

    public Payment find(UUID orderId) throws ResourceNotFoundException {
        if (!payments.containsKey(orderId)) {
            throw new ResourceNotFoundException("Payment with OrderID " + orderId + " cannot be found.");
        }
        return payments.get(orderId);
    }

    public boolean exists(UUID orderId) {
        return payments.containsKey(orderId);
    }

    public void remove(UUID orderId) throws ResourceNotFoundException {
        if (!payments.containsKey(orderId)) {
            throw new ResourceNotFoundException("Payment with OrderId " + orderId + " cannot be found.");
        }
        payments.remove(orderId);
    }
}
