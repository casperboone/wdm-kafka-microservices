package nl.tudelft.wdm.group1.endtoend;
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
    public void createAndCheckoutOrder() throws InterruptedException {
        List<UUID> users = createUsers();
        UUID user0 = users.get(0);
        int credit0 = getUserCredit(user0);
        Assert.assertEquals(0, credit0);

        List<UUID> stocks = createStocks();
        UUID stockItem0 = stocks.get(0);
        int stockItemPrice0 = getStockPrice(stockItem0);
        int stockItemAmount0 = getStockAmount(stockItem0);
        Assert.assertEquals(20, stockItemPrice0);
        Assert.assertEquals(30, stockItemAmount0);

        UUID stockItem1 = stocks.get(1);
        int stockItemPrice1 = getStockPrice(stockItem1);
        int stockItemAmount1 = getStockAmount(stockItem1);
        Assert.assertEquals(21, stockItemPrice1);
        Assert.assertEquals(40, stockItemAmount1);

        UUID stockItem2 = stocks.get(2);
        int stockItemPrice2 = getStockPrice(stockItem2);
        int stockItemAmount2 = getStockAmount(stockItem2);
        Assert.assertEquals(22, stockItemPrice2);
        Assert.assertEquals(50, stockItemAmount2);

        int startCredit = 1000;
        addCredit(users.get(0), startCredit);
        Assert.assertEquals(startCredit, getUserCredit(user0));
        addCredit(users.get(0), startCredit);
        Assert.assertEquals(startCredit * 2, getUserCredit(user0));

        UUID order = createOrder(user0);
        ArrayList<UUID> itemIds;

        itemIds = getOrderItemIds(order);
        Assert.assertEquals(0, itemIds.size());

        // add 2 items
        addOrderItem(order, stockItem0);
        addOrderItem(order, stockItem1);
        itemIds = getOrderItemIds(order);
        Assert.assertEquals(2, itemIds.size());

        // remove 1 item
        deleteOrderItem(order, stockItem1);
        itemIds = getOrderItemIds(order);
        Assert.assertEquals(1, itemIds.size());

        // add 1 item
        addOrderItem(order, stockItem2);
        itemIds = getOrderItemIds(order);
        Assert.assertEquals(2, itemIds.size());

        // make the transaction
        checkoutOrder(order);
        Thread.sleep(2000);

        int newCredit = getUserCredit(user0) - stockItemPrice0 - stockItemPrice2;
        int actualNewCredit = getUserCredit(user0);

        // check values in the system
        Assert.assertEquals(stockItemAmount1, getStockAmount(stockItem1));
        Assert.assertEquals(stockItemAmount0 - 1, getStockAmount(stockItem0));
        Assert.assertEquals(stockItemAmount2 - 1, getStockAmount(stockItem2));
        Assert.assertEquals(actualNewCredit, newCredit);
    }

}
