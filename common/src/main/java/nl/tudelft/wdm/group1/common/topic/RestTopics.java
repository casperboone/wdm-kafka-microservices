package nl.tudelft.wdm.group1.common.topic;

public class RestTopics {
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    public static String[] getTopics() {
        return new String[] {REQUEST, RESPONSE};
    }
}
