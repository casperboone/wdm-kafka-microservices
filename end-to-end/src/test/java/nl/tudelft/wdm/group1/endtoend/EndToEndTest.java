package nl.tudelft.wdm.group1.endToEnd;
import org.junit.Test;
import java.util.*;

public class EndToEndTest extends EndToEndBase {

    @Test
    public void createAndDeleteUser() {
        List<UUID> users = createUsers();
        List<UUID> stocks = createStocks();

        addCredit(users.get(0), 3000);
        addCredit(users.get(1), 4000);

        UUID order = createOrder(users.get(0));

        addOrderItem(order, stocks.get(0));
        addOrderItem(order, stocks.get(1));

        deleteOrderItem(order, stocks.get(0));

        addOrderItem(order, stocks.get(2));

        checkoutOrder(order);

        deleteUsers(users);
    }

}
