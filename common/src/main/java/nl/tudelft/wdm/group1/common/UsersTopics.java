package nl.tudelft.wdm.group1.common;

public class UsersTopics {
    public static final String USER_CREATED = "userCreated";
    public static final String USER_DELETED = "userDeleted";
    public static final String CREDIT_SUBTRACTED = "creditSubtracted";
    public static final String CREDIT_ADDED = "creditAdded";

    public static String[] getTopics() {
        return new String[]{USER_CREATED, USER_DELETED, CREDIT_SUBTRACTED, CREDIT_ADDED};
    }
}
