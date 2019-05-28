package nl.tudelft.wdm.group1.payments;

import nl.tudelft.wdm.group1.common.Payment;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, UUID> {
    default Payment findOrElseThrow(UUID id) throws ResourceNotFoundException {
        return findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment with ID " + id + " cannot be found."));
    }
}
