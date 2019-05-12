package nl.tudelft.wdm.group1.stock;

import java.util.UUID;

public class StockItem {
    private UUID id;

    public StockItem() {
        id = UUID.randomUUID(); // TODO: Remove this line as soon as the non-default constructor is created
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "id=" + id +
                '}';
    }
}
