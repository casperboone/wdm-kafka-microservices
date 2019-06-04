package nl.tudelft.wdm.group1.common.topics;

public class StockTopics {
    public static final String STOCK_ITEM_CREATED = "stockItemCreated";
    public static final String STOCK_ADDED = "stockAdded";
    public static final String STOCK_SUBTRACTED = "stockSubtracted";

    public static String[] getTopics() {
        return new String[] {
                STOCK_ITEM_CREATED,
                STOCK_ADDED,
                STOCK_SUBTRACTED
        };
    }
}
