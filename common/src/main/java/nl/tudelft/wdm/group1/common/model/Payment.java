package nl.tudelft.wdm.group1.common.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class Payment {
    private UUID userId;
    @Id
    private UUID orderId;
    private int amount;

    public Payment() {
    }

    public Payment(UUID userId, UUID orderId, int amount) {
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

    @Override
    public String toString() {
        return "Payment{" +
                "userId=" + userId +
                ", orderId=" + orderId +
                ", amount=" + amount +
                '}';
    }
}
