package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class OrderAddPayload extends RestPayload {
    private UUID userId;

    public OrderAddPayload() {
    }

    public OrderAddPayload(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
