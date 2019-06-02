package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class OrderGetPayload extends RestPayload {
    private UUID id;

    public OrderGetPayload() {
    }

    public OrderGetPayload(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
