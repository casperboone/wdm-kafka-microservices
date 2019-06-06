package nl.tudelft.wdm.group1.payments;

import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.model.Payment;
import nl.tudelft.wdm.group1.common.payload.PaymentAddPayload;
import nl.tudelft.wdm.group1.common.payload.PaymentDeletePayload;
import nl.tudelft.wdm.group1.common.payload.PaymentGetPayload;
import nl.tudelft.wdm.group1.common.topic.RestTopics;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
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

    private static Consumer<String, KafkaResponse<Payment>> defaultConsumer;

    @BeforeClass
    public static void setUpBeforeClass() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("consumer" + UUID.randomUUID().toString(), "false", embeddedKafka.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<KafkaResponse<Payment>> tJsonDeserializer = new JsonDeserializer<>();
        tJsonDeserializer.addTrustedPackages("*");
        defaultConsumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), tJsonDeserializer).createConsumer();
        defaultConsumer.subscribe(Collections.singletonList(RestTopics.RESPONSE));
    }
    @Before
    public void setUp() {
        Payment defaultPayment = new Payment(defaultUserId, defaultOrderId, defaultAmount);
        paymentRepository.save(defaultPayment);
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
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().until(() -> paymentRepository.existsById(orderId));

        Payment payment = paymentRepository.findOrElseThrow(orderId);

        assertThat(payment.getUserId()).isEqualTo(userId);
        assertThat(payment.getOrderId()).isEqualTo(orderId);
        assertThat(payment.getAmount()).isEqualTo(defaultAmount);
    }

    @Test
    public void deleteAPayment() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new PaymentDeletePayload(defaultUserId, defaultOrderId)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().until(() -> !paymentRepository.existsById(defaultOrderId));
    }

    @Test
    public void retrieveAPayment() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new PaymentGetPayload(defaultOrderId)));
        Payment payment = KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE).value().getPayload();

        assertThat(payment.getOrderId()).isEqualTo(defaultOrderId);
    }
}
