package nl.tudelft.wdm.group1.stock.event;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
import nl.tudelft.wdm.group1.stock.events.Consumer;
import nl.tudelft.wdm.group1.stock.events.Producer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(ConcurrentTestRunner.class) // default # threads = 4
public class StockConsumerMultiThreadTest {
    private Producer stockProducer;
    private Consumer stockConsumer;
    private StockItemRepository stockItemRepository;
    private StockItem stockItemMilk;
    private StockItem stockItemJuice;
    private StockItem stockItemCoke;

    @Before
    public void setUp() {
        stockProducer = mock(Producer.class);
        stockItemRepository = mock(StockItemRepository.class);
        stockConsumer = new Consumer(stockItemRepository, stockProducer);

        stockItemMilk = new StockItem(5, "Milk", 1);
        stockItemJuice = new StockItem(4, "Juice", 2);
        stockItemCoke = new StockItem(3, "Coke", 3);
    }


    @Test
    public void testHandleOrderCheckedOutWithSufficientStockForMultipleUsers()
            throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
        // stock items sufficient to meet all user's need

        when(stockItemRepository.find(stockItemMilk.getId())).thenReturn(stockItemMilk);
        when(stockItemRepository.find(stockItemJuice.getId())).thenReturn(stockItemJuice);

        Order order = new Order(UUID.randomUUID());
        order.addItem(stockItemMilk.getId());
        order.addItem(stockItemJuice.getId());

        stockConsumer.consumeOrderCheckedOut(order);

        verify(stockProducer).emitStockItemsSubtractedForOrder(order);

        assertThat(stockItemMilk.getStock()).isEqualTo(1); // 5-4
        assertThat(stockItemJuice.getStock()).isEqualTo(0); // 4-4
        assertThat(order.getPrice()).isEqualTo(3); // 1+2
    }

    // TODO: create a separate class for testHandleOrderCheckedOutWithInsufficientStockForMultipleUsers()
//    @Test
//    public void testHandleOrderCheckedOutWithInsufficientStockForMultipleUsers()
//            throws ResourceNotFoundException, InsufficientStockException, InvalidStockChangeException {
//        // stock items sufficient for one user but not for more than one users
//
//        when(stockItemRepository.find(stockItemMilk.getId())).thenReturn(stockItemMilk);
//        when(stockItemRepository.find(stockItemJuice.getId())).thenReturn(stockItemJuice);
//        when(stockItemRepository.find(stockItemCoke.getId())).thenReturn(stockItemCoke);
//
//        Order order = new Order(UUID.randomUUID());
//        order.addItem(stockItemJuice.getId());
//        order.addItem(stockItemMilk.getId());
//        order.addItem(stockItemCoke.getId());
//
//        stockConsumer.consumeOrderCheckedOut(order);
//        verify(stockProducer).emitStockItemsSubtractedForOrder(order); ??
//        verify(stockProducer).emitStockItemsSubtractForOrderFailed(order); ??
//
//        assertThat(??)
//    }
}
