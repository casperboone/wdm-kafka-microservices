package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class UserCreditSubtractPayload extends RestPayload {
    private UUID userId;
    private int amount;

    public UserCreditSubtractPayload() {

    }

    public UserCreditSubtractPayload(UUID userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public UUID getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }
}
