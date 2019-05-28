package nl.tudelft.wdm.group1.stock.event;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
import nl.tudelft.wdm.group1.stock.events.Consumer;
import nl.tudelft.wdm.group1.stock.events.Producer;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StockConsumerTest {
    private Producer stockProducer;
    private Consumer stockConsumer;
    private StockItemRepository stockItemRepository;

    @Before
    public void setUp() {
        stockProducer = mock(Producer.class);
        stockItemRepository = mock(StockItemRepository.class);
        stockConsumer = new Consumer(stockItemRepository, stockProducer);
    }

    @Test
    public void testHandleOrderCheckedOutWithSufficientStock()
        throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        StockItem stockItem1 = new StockItem(10, "Milk", 1);
        StockItem stockItem2 = new StockItem(5, "Coke", 1);
        when(stockItemRepository.find(any(UUID.class))).thenReturn(stockItem1, stockItem2);

        Order order = new Order(UUID.randomUUID());
        order.addItem(stockItem1.getId());
        order.addItem(stockItem2.getId());

        stockConsumer.consumeOrderCheckedOut(order);

        verify(stockProducer).emitStockItemsSubtractedForOrder(order);
    }

    @Test
    public void testHandleOrderCheckedOutWithInsufficientStock()
        throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        StockItem stockItem1 = new StockItem(0, "Milk", 1);
        StockItem stockItem2 = new StockItem(1, "Coke", 1);
        when(stockItemRepository.find(any(UUID.class))).thenReturn(stockItem1, stockItem2);

        Order order = new Order(UUID.randomUUID());
        order.addItem(stockItem1.getId());
        order.addItem(stockItem2.getId());

        stockConsumer.consumeOrderCheckedOut(order);

        verify(stockProducer).emitStockItemsSubtractForOrderFailed(order);
    }

    @Test
    public void testHandleOrderCheckedOutWithSufficientStockForMultipleUsers()
            throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        // stock items sufficient to meet all user's need

    }

    @Test
    public void testHandleOrderCheckedOutWithInsufficientStockForMultipleUsers()
            throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        // stock items sufficient for one user but not for more than one users

    }

}
