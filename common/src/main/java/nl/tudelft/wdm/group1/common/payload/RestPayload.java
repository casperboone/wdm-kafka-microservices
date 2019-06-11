package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public abstract class RestPayload {
    private UUID requestId;
    private int partition;

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public UUID getRequestId() {
        return requestId;
    }

    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return String.format("RestPayload { requestId = %s, partition = %d }", requestId, partition);
    }
}
