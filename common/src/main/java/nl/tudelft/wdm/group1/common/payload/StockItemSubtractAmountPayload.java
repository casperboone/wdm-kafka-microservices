package nl.tudelft.wdm.group1.common.payload;

import java.util.UUID;

public class StockItemSubtractAmountPayload extends RestPayload {
    private UUID id;
    private int amount;

    public StockItemSubtractAmountPayload() {
    }

    public StockItemSubtractAmountPayload(UUID id, int amount) {
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
