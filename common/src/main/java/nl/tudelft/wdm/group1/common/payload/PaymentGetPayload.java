package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class PaymentGetPayload extends RestPayload {
    private UUID orderId;

    public PaymentGetPayload() {
    }

    public PaymentGetPayload(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getOrderId() {
        return orderId;
    }
}
