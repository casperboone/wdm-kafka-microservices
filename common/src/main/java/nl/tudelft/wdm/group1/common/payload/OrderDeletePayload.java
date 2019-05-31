package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class OrderDeletePayload extends RestPayload {
    private UUID id;

    public OrderDeletePayload() {
    }

    public OrderDeletePayload(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
