package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public abstract class RestPayload {
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    protected UUID requestId;

    public UUID getRequestId() {
        return requestId;
    }
}
