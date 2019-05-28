package nl.tudelft.wdm.group1.common;

public class StockTopics {
    public static final String STOCK_ITEM_CREATED = "stockItemCreated";
    public static final String STOCK_ADDED = "stockAdded";
    public static final String STOCK_SUBTRACTED = "stockSubtracted";
    public static final String ORDER_PROCESSED_IN_STOCK_SUCCESSFUL = "orderProcessedInStockSuccessful";

    public static String[] getTopics() {
        return new String[] {
                STOCK_ITEM_CREATED,
                STOCK_ADDED,
                STOCK_SUBTRACTED,
                ORDER_PROCESSED_IN_STOCK_SUCCESSFUL,
        };
    }
}
