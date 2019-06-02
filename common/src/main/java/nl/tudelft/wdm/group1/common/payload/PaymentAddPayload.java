package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class PaymentAddPayload extends RestPayload {
    private UUID userId;
    private UUID orderId;
    private int amount;

    public PaymentAddPayload() {
    }

    public PaymentAddPayload(UUID userId, UUID orderId, int amount) {
        this.userId = userId;
        this.orderId = orderId;
        this.amount = amount;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public int getAmount() {
        return amount;
    }
}
