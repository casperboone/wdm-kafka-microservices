package nl.tudelft.wdm.group1.common.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity(name = "order_table")
public class Order {
    @Id
    private UUID id;
    private UUID userId;
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<UUID> itemIds;
    private boolean processedInStock;
    private boolean paid;
    private int price;
    private OrderStatus status;

    public Order() {
    }

    public Order(UUID userId) {
        id = UUID.randomUUID();
        this.userId = userId;
        this.itemIds = new HashSet<>();
        this.price = -1;
        this.processedInStock = false;
        this.paid = false;
        this.status = OrderStatus.PROCESSING;
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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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
                ", status=" + status +
                '}';
    }
}
