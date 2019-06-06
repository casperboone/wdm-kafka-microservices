package nl.tudelft.wdm.group1.endtoend;
import java.util.*;
import org.junit.Assert;
import org.junit.Test;

import static org.awaitility.Awaitility.await;

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
        Thread.sleep(2000);
        itemIds = getOrderItemIds(order);
        Assert.assertEquals(1, itemIds.size());
        addOrderItem(order, stockItem1);
        Thread.sleep(2000);
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
        await().until(() -> !getOrderStatus(order).equals("PROCESSING"));
        String status = getOrderStatus(order);

        // check values in the system
        Assert.assertEquals("SUCCEEDED", status);
        Assert.assertEquals(stockItemAmount1, getStockAmount(stockItem1));
        Assert.assertEquals(stockItemAmount0 - 1, getStockAmount(stockItem0));
        Assert.assertEquals(stockItemAmount2 - 1, getStockAmount(stockItem2));
        
        int newCredit = startCredit * 2 - stockItemPrice0 - stockItemPrice2;
        Assert.assertEquals(newCredit, getUserCredit(user0));
    }

    @Test
    public void checkoutOrderInsufficientStock() throws InterruptedException {
        List<UUID> users = createUsers();
        // get the two users
        UUID user0 = users.get(0);
        UUID user1 = users.get(1);

        List<UUID> stocks = createStocksSingleItems();
        UUID stockItem0 = stocks.get(0);

        int startCredit = 1000;
        addCredit(user0, startCredit);
        Assert.assertEquals(startCredit, getUserCredit(user0));
        addCredit(user1, startCredit);
        Assert.assertEquals(startCredit, getUserCredit(user1));

        // FIRST ORDER
        UUID order0= createOrder(user0);

        // add item 0
        addOrderItem(order0, stockItem0);
        Thread.sleep(1000);
        ArrayList<UUID> itemIds0 = getOrderItemIds(order0);
        Assert.assertEquals(1, itemIds0.size());

        // make the transaction
        checkoutOrder(order0);
        await().until(() -> !getOrderStatus(order0).equals("PROCESSING"));
        String status0 = getOrderStatus(order0);

        // check that the item is sold out
        Assert.assertEquals("SUCCEEDED", status0);
        Assert.assertEquals(0, getStockAmount(stockItem0));
        Assert.assertNotEquals(startCredit, getUserCredit(user0));

        // SECOND ORDER
        UUID order1 = createOrder(user0);

        // add item 0
        addOrderItem(order1, stockItem0);
        Thread.sleep(1000);
        ArrayList<UUID> itemIds1 = getOrderItemIds(order1);
        Assert.assertEquals(1, itemIds1.size());

        // make the transaction
        checkoutOrder(order1);
        await().until(() -> !getOrderStatus(order1).equals("PROCESSING"));
        String status1 = getOrderStatus(order1);

        // check that the item is sold out
        Assert.assertEquals("FAILED_DUE_TO_LACK_OF_STOCK", status1);
        Assert.assertEquals(0, getStockAmount(stockItem0));
        Assert.assertEquals(startCredit, getUserCredit(user1));
    }

    @Test
    public void checkoutOrderInsufficientMoneys() throws InterruptedException {
        List<UUID> users = createUsers();
        // get the user
        UUID user0 = users.get(0);

        List<UUID> stocks = createStocks();
        UUID stockItem0 = stocks.get(0);
        int stockItemAmount0 = getStockAmount(stockItem0);
        UUID stockItem1 = stocks.get(1);
        int stockItemAmount1 = getStockAmount(stockItem1);
        UUID stockItem2 = stocks.get(2);
        int stockItemAmount2 = getStockAmount(stockItem2);

        // Assert that the user is broke
        Assert.assertEquals(0, getUserCredit(user0));
        int startCredit = 2;
        addCredit(users.get(0), startCredit);
        Assert.assertEquals(startCredit, getUserCredit(user0));

        // make order
        UUID order0= createOrder(user0);

        // add items 0,1 and 2
        addOrderItem(order0, stockItem0);
        Thread.sleep(1000);
        addOrderItem(order0, stockItem1);
        Thread.sleep(1000);
        addOrderItem(order0, stockItem2);
        Thread.sleep(1000);
        ArrayList<UUID> itemIds0 = getOrderItemIds(order0);
        Assert.assertEquals(3, itemIds0.size());

        // make the transaction
        checkoutOrder(order0);
        await().until(() -> !(getOrderStatus(order0).equals("PROCESSING")));
        String status0 = getOrderStatus(order0);

        // check that the order is rejected due to lack of credit
        Assert.assertEquals("FAILED_DUE_TO_LACK_OF_PAYMENT", status0);
        Assert.assertEquals(stockItemAmount0, getStockAmount(stockItem0));
        Assert.assertEquals(stockItemAmount1, getStockAmount(stockItem1));
        Assert.assertEquals(stockItemAmount2, getStockAmount(stockItem2));
        Assert.assertEquals(startCredit, getUserCredit(user0));
    }
}
