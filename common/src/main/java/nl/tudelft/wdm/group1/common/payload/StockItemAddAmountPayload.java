package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class StockItemAddAmountPayload extends RestPayload {
    private UUID id;
    private int amount;

    public StockItemAddAmountPayload() {
    }

    public StockItemAddAmountPayload(UUID id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public UUID getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }
}
