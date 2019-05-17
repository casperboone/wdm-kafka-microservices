package nl.tudelft.wdm.group1.stock;

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

    public void addStock(final int amount) throws InvalidStockChangeException {
        if (amount < 0) {
            throw new InvalidStockChangeException("Cannot add a negative value");
        }
        this.stock += amount;
    }

    public void subtractStock(final int amount)
            throws InvalidStockChangeException, InsufficientStockException{
        if (amount < 0) {
            throw new InvalidStockChangeException("Cannot subtract a negative value");
        }

        if (amount > this.stock) {
            throw new InsufficientStockException("Insufficient stock");
        }

        this.stock -= amount;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "id=" + this.id +
                "stock=" + this.stock +
                '}';
    }
}
