package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class OrderCheckoutPayload extends RestPayload {
    private UUID id;

    public OrderCheckoutPayload() {
    }

    public OrderCheckoutPayload(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
