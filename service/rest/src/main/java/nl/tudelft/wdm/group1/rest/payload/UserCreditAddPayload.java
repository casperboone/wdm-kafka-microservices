package nl.tudelft.wdm.group1.rest.payload;

import nl.tudelft.wdm.group1.common.payload.RestPayload;

import java.util.UUID;

public class UserCreditAddPayload extends RestPayload {
    private UUID userId;
    private int amount;

    public UserCreditAddPayload() {

    }

    public UserCreditAddPayload(UUID userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public UUID getUserId() {
        return userId;
    }
}
