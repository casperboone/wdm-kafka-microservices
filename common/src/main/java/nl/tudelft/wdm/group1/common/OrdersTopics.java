package nl.tudelft.wdm.group1.common;

public class OrdersTopics {
    public static final String ORDER_CHECKED_OUT = "orderCheckedOut";
    public static final String ORDER_CREATED = "orderCreated";
    public static final String ORDER_DELETED = "orderDeleted";
    public static final String ORDER_ITEM_ADDED = "orderItemAdded";
    public static final String ORDER_ITEM_DELETED = "orderItemDeleted";
    public static final String ORDER_READY = "orderReady";
    public static final String ORDER_PROCESSED_IN_STOCK_SUCCESSFUL = "orderProcessedInStockSuccessful";
    public static final String ORDER_PROCESSED_IN_STOCK_FAILED = "orderProcessedInStockFailed";

    public static String[] getTopics() {
        return new String[] {
                ORDER_CHECKED_OUT,
                ORDER_CREATED,
                ORDER_DELETED,
                ORDER_ITEM_ADDED,
                ORDER_ITEM_DELETED,
                ORDER_READY,
                ORDER_PROCESSED_IN_STOCK_SUCCESSFUL,
                ORDER_PROCESSED_IN_STOCK_FAILED,
        };
    }
}
