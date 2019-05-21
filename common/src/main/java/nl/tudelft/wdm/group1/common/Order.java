package nl.tudelft.wdm.group1.common;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Order {
    private UUID id;
    private UUID userId;
    private Set<UUID> itemIds;

    public Order() {
    }

    public Order(UUID userId) {
        id = UUID.randomUUID();
        this.userId = userId;
        this.itemIds = new HashSet<>();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    // mock method
    public int getAmount() {
        return 42;
    }

    public Set<UUID> getItemIds() {
        return itemIds;
    }

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
