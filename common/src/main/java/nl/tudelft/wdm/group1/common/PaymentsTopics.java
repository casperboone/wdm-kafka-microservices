package nl.tudelft.wdm.group1.common;

public class PaymentsTopics {
    public static final String PAYMENT_CREATED = "paymentCreated";
    public static final String PAYMENT_DELETED = "paymentDeleted";
    public static final String PAYMENT_SUCCESSFUL = "paymentSuccessful";

    public static String[] getTopics() {
        return new String[] {PAYMENT_CREATED, PAYMENT_DELETED, PAYMENT_SUCCESSFUL};
    }
}