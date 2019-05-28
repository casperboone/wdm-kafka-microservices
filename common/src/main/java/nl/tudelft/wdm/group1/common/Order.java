package nl.tudelft.wdm.group1.common;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Order {
    private UUID id;
    private UUID userId;
    private Set<UUID> itemIds;
    private boolean processedInStock;
    private boolean paid;
    private int price;

    public Order() {
    }

    public Order(UUID userId) {
        id = UUID.randomUUID();
        this.userId = userId;
        this.itemIds = new HashSet<>();
        this.price = -1;
        this.processedInStock = false;
        this.paid = false;
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public Set<UUID> getItemIds() {
        return itemIds;
    }

    public boolean isProcessedInStock() {
        return processedInStock;
    }

    public boolean isPaid() {
        return paid;
    }

    public int getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }

    public void addItem(UUID itemId) {
        itemIds.add(itemId);
    }

    public void deleteItem(UUID itemId) {
        itemIds.remove(itemId);
    }

    public void setProcessedInStock(boolean processedInStock) {
        this.processedInStock = processedInStock;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", itemIds=" + itemIds +
                ", processedInStock=" + processedInStock +
                ", paid=" + paid +
                ", price=" + price +
                '}';
    }
}
