package nl.tudelft.wdm.group1.common.payload;

public class StockItemCreatePayload extends RestPayload {
    private int stock;
    private String name;
    private int price;

    public StockItemCreatePayload() {
    }

    public StockItemCreatePayload(int stock, String name, int price) {
        this.stock = stock;
        this.name = name;
        this.price = price;
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
}
