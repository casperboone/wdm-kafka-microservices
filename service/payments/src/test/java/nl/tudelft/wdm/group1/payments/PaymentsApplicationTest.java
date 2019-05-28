package nl.tudelft.wdm.group1.payments;

import nl.tudelft.wdm.group1.common.Payment;
import nl.tudelft.wdm.group1.common.RestTopics;
import nl.tudelft.wdm.group1.common.payload.PaymentAddPayload;
import nl.tudelft.wdm.group1.common.payload.PaymentDeletePayload;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        properties = {
                "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
                "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        }
)
public class PaymentsApplicationTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 5, "payments");

    private UUID defaultUserId = UUID.randomUUID();
    private UUID defaultOrderId = UUID.randomUUID();
    private int defaultAmount = 100;

    @Before
    public void setUp() {
        Payment defaultPayment = new Payment(defaultUserId, defaultOrderId, defaultAmount);
        paymentRepository.add(defaultPayment);
    }

    private static <V> Producer<String, V> createProducer() {
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
        return new DefaultKafkaProducerFactory<String, V>(senderProps, new StringSerializer(), new JsonSerializer<>()).createProducer();
    }

    @Test
    public void createNewPayment() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new PaymentAddPayload(userId, orderId, defaultAmount))).get();

        await().ignoreExceptions().until(() -> null != paymentRepository.find(orderId));

        Payment payment = paymentRepository.find(orderId);

        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getAmount()).isEqualTo(defaultAmount);
    }

    @Test
    public void deleteAPayment() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new PaymentDeletePayload(defaultUserId, defaultOrderId)));

        await().until(() -> !paymentRepository.exists(defaultOrderId));
    }
}
