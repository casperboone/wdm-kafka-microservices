package nl.tudelft.wdm.group1.stock;

import jdk.nashorn.internal.objects.annotations.Constructor;

import java.util.UUID;

public class StockItem {
    private UUID id;
    private int stock;

    public StockItem() {

    }

    public StockItem(final int stock) {
        this.id = UUID.randomUUID();
        this.stock = stock;
    }

    public UUID getId() {
        return id;
    }

    public int getStock() { return this.stock; }

    public void addStock(final int amount) {
        if (amount > 0) this.stock += amount; // TODO: maybe create a new exception for this?
    }

    public void subtractStock(final int amount) throws InsufficientStockException{
        if (amount > this.stock) {
            throw new InsufficientStockException("Insufficient stock");
        }

        if (amount > 0) {
            this.stock -= amount;
        }
    }


    @Override
    public String toString() {
        return "StockItem{" +
                "id=" + this.id +
                "stock=" + this.stock +
                '}';
    }
}
