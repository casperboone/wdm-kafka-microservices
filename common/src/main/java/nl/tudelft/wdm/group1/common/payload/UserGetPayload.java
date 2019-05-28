package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class UserGetPayload extends RestPayload {
    private UUID userId;

    public UserGetPayload() {

    }

    public UserGetPayload(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId() {
        return userId;
    }
}
