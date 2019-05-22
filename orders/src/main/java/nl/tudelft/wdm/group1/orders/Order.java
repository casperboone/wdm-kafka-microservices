package nl.tudelft.wdm.group1.orders;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Order {
    private UUID id;
    private UUID userId;
    private Set<UUID> itemIds;
    private int price;

    public Order() {
    }

    public Order(UUID userId) {
        id = UUID.randomUUID();
        this.userId = userId;
        this.itemIds = new HashSet<>();
        this.price = -1;
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

    public int getPrice() { return price; }

    public void setPrice(int price) { this.price = price; }

    public void addItem(UUID itemId) {
        itemIds.add(itemId);
    }

    public void deleteItem(UUID itemId) {
        itemIds.remove(itemId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", itemIds=" + itemIds +
                '}';
    }
}
