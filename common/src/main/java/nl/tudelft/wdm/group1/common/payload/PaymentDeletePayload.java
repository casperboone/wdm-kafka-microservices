package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class PaymentDeletePayload extends RestPayload {
    private UUID userId;
    private UUID orderId;

    public PaymentDeletePayload() {
    }

    public PaymentDeletePayload(UUID userId, UUID orderId) {
        this.userId = userId;
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
