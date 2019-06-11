package nl.tudelft.wdm.group1.common.topic;

public class RestTopics {
    public static final String USERS_REQUEST = "usersRequest";
    public static final String STOCK_REQUEST = "stockRequest";
    public static final String PAYMENT_REQUEST = "paymentRequest";
    public static final String ORDERS_REQUEST = "ordersRequest";
    public static final String RESPONSE = "response";

    public static String[] getTopics() {
        return new String[] {USERS_REQUEST, STOCK_REQUEST, PAYMENT_REQUEST, ORDERS_REQUEST, RESPONSE};
    }
}
