package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class OrderItemDeletePayload extends RestPayload {
    private UUID id;
    private UUID itemId;

    public OrderItemDeletePayload() {
    }

    public OrderItemDeletePayload(UUID id, UUID itemId) {
        this.id = id;
        this.itemId = itemId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getItemId() {
        return itemId;
    }
}

