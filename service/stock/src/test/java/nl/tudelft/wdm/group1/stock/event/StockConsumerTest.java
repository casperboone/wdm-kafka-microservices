package nl.tudelft.wdm.group1.stock.event;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.stock.StockItemRepository;
import nl.tudelft.wdm.group1.stock.events.Consumer;
import nl.tudelft.wdm.group1.stock.events.Producer;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(stockItemRepository.findOrElseThrow(any(UUID.class))).thenReturn(stockItem1, stockItem2);

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
        when(stockItemRepository.findOrElseThrow(any(UUID.class))).thenReturn(stockItem1, stockItem2);

        Order order = new Order(UUID.randomUUID());
        order.addItem(stockItem1.getId());
        order.addItem(stockItem2.getId());

        stockConsumer.consumeOrderCheckedOut(order);

        verify(stockProducer).emitStockItemsSubtractForOrderFailed(order);
    }

    @Test
    public void testHandleOutOfStock() throws ResourceNotFoundException {
        Order order = new Order(UUID.randomUUID());
        StockItem stockItem = new StockItem(1, "name", 1);
        UUID stockItemId = stockItem.getId();
        order.addItem(stockItem.getId());
        order.setStatus(OrderStatus.FAILED_DUE_TO_LACK_OF_PAYMENT);
        when(stockItemRepository.findOrElseThrow(stockItemId)).thenReturn(stockItem);
        stockConsumer.consumeOrderCancelled(order);
        verify(stockItemRepository).findOrElseThrow(stockItemId);
        assertThat(stockItem.getStock()).isEqualTo(2);
    }
}
