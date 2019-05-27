package nl.tudelft.wdm.group1.payments.events;

import nl.tudelft.wdm.group1.common.Order;
import nl.tudelft.wdm.group1.common.Payment;
import nl.tudelft.wdm.group1.payments.PaymentRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ConsumerTest {
    private OrderConsumer orderConsumer;
    private UserConsumer userConsumer;
    private Producer producer;
    private PaymentRepository paymentRepository;

    @Before
    public void setUp() {
        producer = mock(Producer.class);
        paymentRepository = mock(PaymentRepository.class);
        orderConsumer = new OrderConsumer(paymentRepository, producer);
        userConsumer = new UserConsumer(paymentRepository, producer);
    }

    @Test
    public void testHandleOrderCheckoutCheck() {
        Order order = new Order(UUID.randomUUID());
        orderConsumer.consume(order);
        verify(producer).emitPaymentCreated(any());
    }

    @Test
    public void testHandleCreditSuccessful() {
        Payment payment = new Payment(UUID.randomUUID(), UUID.randomUUID(), 42);
        userConsumer.consume(payment);
        verify(producer).emitPaymentSuccessful(payment);
    }
}