package nl.tudelft.wdm.group1.endToEnd;
import org.junit.Assert;
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

    @Test
    public void createAndCheckoutOrder() {
        List<UUID> users = createUsers();
        List<UUID> stocks = createStocks();

        UUID user0 = users.get(0);
        UUID stockItem0 = stocks.get(0);
        int stockItemPrice0 = getStockPrice(stockItem0);
        UUID stockItem1 = stocks.get(1);
        int stockItemPrice1 = getStockPrice(stockItem1);
        UUID stockItem2 = stocks.get(2);
        int stockItemPrice2 = getStockPrice(stockItem2);

        int startCredit = 3000;

        addCredit(users.get(0), startCredit);
        UUID order = createOrder(user0);

        addOrderItem(order, stockItem0);
        addOrderItem(order, stockItem1);
        deleteOrderItem(order, stockItem1);

        addOrderItem(order, stockItem2);
        checkoutOrder(order);

        int newCredit = getUserCredit(user0) - stockItemPrice0 - stockItemPrice2;
        int actualNewCredit = getUserCredit(user0);
        Assert.assertEquals(newCredit, actualNewCredit);
    }

}
