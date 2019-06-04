package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class OrderReadyPayload extends RestPayload {
    private UUID id;

    public OrderReadyPayload() {
    }

    public OrderReadyPayload(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
