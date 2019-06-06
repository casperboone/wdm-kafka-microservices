package nl.tudelft.wdm.group1.common.model;

import nl.tudelft.wdm.group1.common.exception.InsufficientStockException;
import nl.tudelft.wdm.group1.common.exception.InvalidStockChangeException;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@Entity
public class StockItem {
    @Id
    private UUID id;
    private int stock;
    private String name;
    private int price;

    public StockItem() {

    }

    public StockItem(final int stock, final String name, final int price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public UUID getId() {
        return id;
    }

    public int getStock() {
        return stock;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void addStock(final int amount) throws InvalidStockChangeException {
        if (amount < 0) {
            throw new InvalidStockChangeException("Cannot add a negative value");
        }
        this.stock += amount;
    }

    public void subtractStock(final int amount)
            throws InvalidStockChangeException, InsufficientStockException {
        if (amount < 0) {
            throw new InvalidStockChangeException("Cannot subtract a negative value");
        }

        if (amount > this.stock) {
            throw new InsufficientStockException("Insufficient stock. " + this.name + " - available stock: " + this.stock);
        }

        this.stock -= amount;
    }

    @Override
    public String toString() {
        return "StockItem{" +
                "id=" + id +
                ", stock=" + stock +
                ", name='" + name + "'" +
                ", price=" + price +
                '}';
    }
}
