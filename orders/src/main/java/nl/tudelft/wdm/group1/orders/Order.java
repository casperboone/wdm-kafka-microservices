package nl.tudelft.wdm.group1.orders;

import java.util.UUID;

public class Order {
    private UUID id;

    public Order() {
        id = UUID.randomUUID(); // TODO: Remove this line as soon as the non-default constructor is created
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                '}';
    }
}
