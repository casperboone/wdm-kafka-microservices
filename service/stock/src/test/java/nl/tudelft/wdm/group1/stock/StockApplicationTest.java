package nl.tudelft.wdm.group1.stock;

import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.model.StockItem;
import nl.tudelft.wdm.group1.common.payload.StockItemAddAmountPayload;
import nl.tudelft.wdm.group1.common.payload.StockItemCreatePayload;
import nl.tudelft.wdm.group1.common.payload.StockItemGetPayload;
import nl.tudelft.wdm.group1.common.payload.StockItemSubtractAmountPayload;
import nl.tudelft.wdm.group1.common.topics.RestTopics;
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
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext
public class StockApplicationTest {

    @Autowired
    private StockItemRepository stockItemRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 5, "stock");

    private StockItem defaultStockItem;

    private static Consumer<String, KafkaResponse<StockItem>> defaultConsumer;

    @BeforeClass
    public static void setUpBeforeClass() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("consumer" + UUID.randomUUID().toString(), "false", embeddedKafka.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<KafkaResponse<StockItem>> tJsonDeserializer = new JsonDeserializer<>();
        tJsonDeserializer.addTrustedPackages("*");
        defaultConsumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), tJsonDeserializer).createConsumer();
        defaultConsumer.subscribe(Collections.singletonList(RestTopics.RESPONSE));
    }

    @Before
    public void setUp() {
        defaultStockItem = new StockItem(100, "item1", 1000);
        stockItemRepository.save(defaultStockItem);
    }

    private static <V> Producer<String, V> createProducer() {
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
        return new DefaultKafkaProducerFactory<String, V>(senderProps, new StringSerializer(), new JsonSerializer<>()).createProducer();
    }

    @Test
    public void createNewStockItem() throws Exception {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new StockItemCreatePayload(99, "item2", 999)));
        StockItem result = KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE).value().getPayload();

        await().until(() -> stockItemRepository.existsById(result.getId()));

        StockItem stockItem = stockItemRepository.findOrElseThrow(result.getId());

        assertThat(stockItem.getStock()).isEqualTo(99);
        assertThat(stockItem.getName()).isEqualTo("item2");
        assertThat(stockItem.getPrice()).isEqualTo(999);
    }

    @Test
    public void retrieveAStockItem() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new StockItemGetPayload(defaultStockItem.getId())));
        StockItem result = KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE).value().getPayload();

        assertThat(result.getStock()).isEqualTo(100);
        assertThat(result.getName()).isEqualTo("item1");
        assertThat(result.getPrice()).isEqualTo(1000);
    }

    @Test
    public void addStock() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new StockItemAddAmountPayload(defaultStockItem.getId(), 100)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().until(() -> stockItemRepository.findOrElseThrow(defaultStockItem.getId()).getStock() == 200);
    }

    @Test
    public void addNegativeStockAmount() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new StockItemAddAmountPayload(defaultStockItem.getId(), -100)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        assertThat(defaultStockItem.getStock()).isEqualTo(100); //100 remains unchanged
    }

    @Test
    public void subtractStock() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new StockItemSubtractAmountPayload(defaultStockItem.getId(), 10)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().until(() -> stockItemRepository.findOrElseThrow(defaultStockItem.getId()).getStock() == 90);
    }

    @Test
    public void subtractNegativeStockAmount() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new StockItemSubtractAmountPayload(defaultStockItem.getId(), -10)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        assertThat(defaultStockItem.getStock()).isEqualTo(100); //100 remains unchanged
    }
}
