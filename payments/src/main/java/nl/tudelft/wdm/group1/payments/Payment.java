package nl.tudelft.wdm.group1.payments;

import java.util.UUID;

public class Payment {
    private UUID id;
    private UUID userId;
    private UUID orderId;

    public Payment(UUID userId, UUID orderId) {
        id = UUID.randomUUID();
        this.userId = userId;
        this.orderId = orderId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Set<UUID> getOrderId() {
        return orderId;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                "userId=" + userId +
                "orderId=" + orderId +
                '}';
    }
}
