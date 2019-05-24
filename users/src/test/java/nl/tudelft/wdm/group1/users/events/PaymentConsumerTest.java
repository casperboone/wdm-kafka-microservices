package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.CreditChangeInvalidException;
import nl.tudelft.wdm.group1.common.Payment;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.User;
import nl.tudelft.wdm.group1.users.UserRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentConsumerTest {
    private PaymentConsumer paymentConsumer;
    private PaymentProducer paymentProducer;
    private UserRepository userRepository;

    @Before
    public void setUp() {
        paymentProducer = mock(PaymentProducer.class);
        userRepository = mock(UserRepository.class);
        paymentConsumer = new PaymentConsumer(userRepository, paymentProducer);
    }

    @Test
    public void testHandlePaymentCreatedWithSufficientBalance() throws ResourceNotFoundException, CreditChangeInvalidException {
        User user = new User("John", "Doe", "Mekelweg 4", "2628 CD", "Delft");
        user.addCredit(3000);
        when(userRepository.find(any(UUID.class))).thenReturn(user);

        Payment payment = new Payment(UUID.randomUUID(), user.getId(), 1500);

        paymentConsumer.consumePaymentCreated(payment);

        verify(paymentProducer).emitCreditSubtractedForPayment(payment);
    }

    @Test
    public void testHandlePaymentCreatedWithInsufficientBalance() throws ResourceNotFoundException, CreditChangeInvalidException {
        User user = new User("John", "Doe", "Mekelweg 4", "2628 CD", "Delft");
        user.addCredit(3000);
        when(userRepository.find(any(UUID.class))).thenReturn(user);

        Payment payment = new Payment(UUID.randomUUID(), user.getId(), 6000);

        paymentConsumer.consumePaymentCreated(payment);

        verify(paymentProducer).emitCreditSubtractionForPaymentFailed(payment);
    }
}
