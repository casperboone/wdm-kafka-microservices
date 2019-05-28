package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class UserDeletePayload extends RestPayload {
    private UUID userId;

    public UserDeletePayload() {

    }

    public UserDeletePayload(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
