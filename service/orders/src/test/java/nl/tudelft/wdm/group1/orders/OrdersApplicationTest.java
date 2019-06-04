package nl.tudelft.wdm.group1.orders;

import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.Order;
import nl.tudelft.wdm.group1.common.payload.OrderAddPayload;
import nl.tudelft.wdm.group1.common.payload.OrderDeletePayload;
import nl.tudelft.wdm.group1.common.payload.OrderItemAddPayload;
import nl.tudelft.wdm.group1.common.payload.OrderItemDeletePayload;
import nl.tudelft.wdm.group1.common.topics.RestTopics;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Before;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
public class OrdersApplicationTest {

    @Autowired
    private OrderRepository orderRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 5, "orders");

    private Order defaultOrder;
    private UUID defaultOrderItemId = UUID.randomUUID();

    @Before
    public void setUp() throws Exception {
        defaultOrder = new Order(UUID.randomUUID());
        defaultOrder.addItem(defaultOrderItemId);
        orderRepository.save(defaultOrder);
        assertThat(orderRepository.findOrElseThrow(defaultOrder.getId())).isNotNull();
    }

    private static <V> Producer<String, V> createProducer() {
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
        return new DefaultKafkaProducerFactory<String, V>(senderProps, new StringSerializer(), new JsonSerializer<>()).createProducer();
    }

    private static <T> org.apache.kafka.clients.consumer.Consumer<String, T> createConsumer() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("consumer" + UUID.randomUUID().toString(), "false", embeddedKafka.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<T> tJsonDeserializer = new JsonDeserializer<>();
        tJsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), tJsonDeserializer).createConsumer();
    }

    @Test
    public void createNewOrder() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new OrderAddPayload(defaultOrder.getUserId())));
        Consumer<String, KafkaResponse<Order>> consumer = createConsumer();
        consumer.subscribe(Collections.singleton(RestTopics.RESPONSE));
        ConsumerRecord<String, KafkaResponse<Order>> orderRecord = KafkaTestUtils.getSingleRecord(consumer, RestTopics.RESPONSE);

        Order order = orderRecord.value().getPayload();

        await().until(() -> orderRepository.existsById(order.getId()));

        assertThat(order.getUserId()).isEqualTo(defaultOrder.getUserId());
        assertThat(order.getItemIds()).isEmpty();
    }

    @Test
    public void removeAnOrder() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new OrderDeletePayload(defaultOrder.getId())));

        await().untilAsserted(() -> assertThatThrownBy(() -> orderRepository.findOrElseThrow(defaultOrder.getId()))
                .isInstanceOf(ResourceNotFoundException.class));
    }

    @Test
    public void addAnItemToAnOrder() {
        UUID newItemId = UUID.randomUUID();

        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new OrderItemAddPayload(defaultOrder.getId(), newItemId)));

        await().untilAsserted(() -> assertThat(orderRepository.findOrElseThrow(defaultOrder.getId()).getItemIds()).contains(defaultOrderItemId, newItemId));
    }

    @Test
    public void removeAnItemFromAnOrder() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new OrderItemDeletePayload(defaultOrder.getId(), defaultOrderItemId)));

        await().untilAsserted(() -> assertThat(orderRepository.findOrElseThrow(defaultOrder.getId()).getItemIds()).isEmpty());
    }
}
