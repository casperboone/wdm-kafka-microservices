package nl.tudelft.wdm.group1.common.topic;

public class RestTopics {
    public static final String USERS_REQUEST = "usersRequest";
    public static final String PAYMENTS_REQUEST = "paymentsRequest";
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    public static String[] getTopics() {
        return new String[] {
                USERS_REQUEST,
                PAYMENTS_REQUEST,
                REQUEST,
                RESPONSE
        };
    }
}
