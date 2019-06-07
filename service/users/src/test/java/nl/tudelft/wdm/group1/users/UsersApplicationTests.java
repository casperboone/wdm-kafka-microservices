package nl.tudelft.wdm.group1.users;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.common.exception.CreditChangeInvalidException;
import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.User;
import nl.tudelft.wdm.group1.common.payload.*;
import nl.tudelft.wdm.group1.common.topic.RestTopics;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
public class UsersApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 1, "users3");

    private User defaultUser;

    private static Consumer<String, KafkaResponse<User>> defaultConsumer;

    @BeforeClass
    public static void setUpBeforeClass() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("consumer" + UUID.randomUUID().toString(), "false", embeddedKafka.getEmbeddedKafka());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<KafkaResponse<User>> tJsonDeserializer = new JsonDeserializer<>();
        tJsonDeserializer.addTrustedPackages("*");
        defaultConsumer = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), tJsonDeserializer).createConsumer();
        defaultConsumer.subscribe(Collections.singletonList(RestTopics.RESPONSE));
    }

    @Before
    public void setUp() throws CreditChangeInvalidException {
        defaultUser = new User("John", "Doe", "Mekelweg 4", "2628 CD", "Delft");
        defaultUser.addCredit(2249);
        userRepository.save(defaultUser);
    }

    private static <V> Producer<String, V> createProducer() {
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka.getEmbeddedKafka());
        return new DefaultKafkaProducerFactory<String, V>(senderProps, new StringSerializer(), new JsonSerializer<>()).createProducer();
    }

    @Test
    public void createNewUser() throws Exception {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new UserCreatePayload(UUID.randomUUID(), "Jane", "Da", "Main Street", "90101", "Rome")));
        ConsumerRecord<String, KafkaResponse<User>> userRecord = KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);
        User result = userRecord.value().getPayload();

        await().until(() -> userRepository.existsById(result.getId()));

        User user = userRepository.findOrElseThrow(result.getId());

        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Da");
        assertThat(user.getStreet()).isEqualTo("Main Street");
        assertThat(user.getZip()).isEqualTo("90101");
        assertThat(user.getCity()).isEqualTo("Rome");
        assertThat(user.getCredit()).isEqualTo(0);
    }

    @Test
    public void retrieveAUser() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new UserGetPayload(defaultUser.getId())));
        ConsumerRecord<String, KafkaResponse<User>> userRecord = KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);
        User user = userRecord.value().getPayload();

        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
        assertThat(user.getStreet()).isEqualTo("Mekelweg 4");
        assertThat(user.getZip()).isEqualTo("2628 CD");
        assertThat(user.getCity()).isEqualTo("Delft");
        assertThat(user.getCredit()).isEqualTo(2249);
    }

    @Test
    public void removeAUser() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new UserDeletePayload(defaultUser.getId())));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().untilAsserted(() -> assertThatThrownBy(() -> userRepository.findOrElseThrow(defaultUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class));
    }

    @Test
    public void addCredit() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new UserCreditAddPayload(defaultUser.getId(), 1500)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().untilAsserted(() -> assertThat(userRepository.findOrElseThrow(defaultUser.getId()).getCredit()).isEqualTo(3749));
    }

    @Test
    public void addNegativeCreditAmount() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new UserCreditAddPayload(defaultUser.getId(), -1500)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().until(() -> userRepository.findOrElseThrow(defaultUser.getId()).getCredit() == 2249);
    }

    @Test
    public void subtractCreditWhenAvailable() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new UserCreditSubtractPayload(defaultUser.getId(), 1500)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().until(() -> userRepository.findOrElseThrow(defaultUser.getId()).getCredit() == 749);
    }

    @Test
    public void subtractCreditWhenNotAvailable() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new UserCreditSubtractPayload(defaultUser.getId(), 3000)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().until(() -> userRepository.findOrElseThrow(defaultUser.getId()).getCredit() == 2249);
    }

    @Test
    public void subtractNegativeCreditAmount() {
        createProducer().send(new ProducerRecord<>(RestTopics.REQUEST, "", new UserCreditSubtractPayload(defaultUser.getId(), -1500)));
        KafkaTestUtils.getSingleRecord(defaultConsumer, RestTopics.RESPONSE);

        await().until(() -> userRepository.findOrElseThrow(defaultUser.getId()).getCredit() == 2249);
    }
}
