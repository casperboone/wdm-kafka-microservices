package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class PaymentGetPayload extends RestPayload {
    private UUID userId;
    private UUID orderId;

    public PaymentGetPayload() {
    }

    public PaymentGetPayload(UUID userId, UUID orderId) {
        this.userId = userId;
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public UUID getUserId() {
        return userId;
    }
}
