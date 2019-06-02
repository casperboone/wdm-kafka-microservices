package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class StockItemGetPayload extends RestPayload {
    private UUID id;

    public StockItemGetPayload() {
    }

    public StockItemGetPayload(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
