package nl.tudelft.wdm.group1.payments;

import java.util.UUID;

public class Payment {
    private UUID id;

    public Payment() {
        id = UUID.randomUUID(); // TODO: Remove this line as soon as the non-default constructor is created
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                '}';
    }
}
