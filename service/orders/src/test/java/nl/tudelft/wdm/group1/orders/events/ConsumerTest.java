package nl.tudelft.wdm.group1.orders.events;

import nl.tudelft.wdm.group1.common.Order;
import nl.tudelft.wdm.group1.common.OrderStatus;
import nl.tudelft.wdm.group1.orders.OrderRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConsumerTest {
    private Consumer consumer;
    private Producer producer;
    private OrderRepository orderRepository;

    @Before
    public void setUp() {
        producer = mock(Producer.class);
        orderRepository = mock(OrderRepository.class);
        consumer = new Consumer(orderRepository, producer);
    }

    @Test
    public void testHandleOrderProcessedInStockSuccessful() {
        Order order = new Order(UUID.randomUUID());
        consumer.consumeOrderProcessedInStockSuccessful(order);
        assertThat(order.isProcessedInStock()).isTrue();
        verify(orderRepository).addOrReplace(order);
        verify(producer).emitOrderCheckedOut(order);
    }

    @Test
    public void testHandlePaymentSuccessful() {
        Order order = new Order(UUID.randomUUID());
        consumer.consumePaymentSuccessful(order);
        assertThat(order.isPaid()).isTrue();
        verify(orderRepository).addOrReplace(order);
    }

    @Test
    public void testHandleOrderProcessedInStockFailed() {
        Order order = new Order(UUID.randomUUID());
        consumer.consumeOrderProcessedInStockFailed(order);
        assertThat(order.getStatus()).isEqualByComparingTo(OrderStatus.FAILEDDUETOLACKOFSTOCK);
        verify(producer).emitOrderCancelled(order);
    }

    @Test
    public void testHandleOrderProcessedPaymentFailed() {
        Order order = new Order(UUID.randomUUID());
        consumer.consumePaymentFailed(order);
        assertThat(order.getStatus()).isEqualByComparingTo(OrderStatus.FAILEDDUETOLACKOFPAYMENT);
        verify(producer).emitOrderCancelled(order);
    }
}
