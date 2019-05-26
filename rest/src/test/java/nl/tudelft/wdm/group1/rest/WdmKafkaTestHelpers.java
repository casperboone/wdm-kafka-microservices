package nl.tudelft.wdm.group1.rest;

import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.RestTopics;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.Function;


public class WdmKafkaTestHelpers {
    private static final Logger logger = LoggerFactory.getLogger(WdmKafkaTestHelpers.class);

    /**
     * Setup Kafka to respond on a specific event.
     * @param embeddedKafka The embedded kafka instance from the tests.
     * @param precondition Function to determine if a given input is relevant for this input.
     * @param transformation Function which return the value expected from kafka.
     * @param <T> Type of the input value.
     */
    public static <T> void setupKafkaResponse(EmbeddedKafkaBroker embeddedKafka, Function<T, Boolean> precondition, Function<T, KafkaResponse> transformation) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Producer<String, KafkaResponse> producer = createProducer(embeddedKafka);
            Consumer<String, T> consumer = createConsumer(embeddedKafka);
            consumer.subscribe(Collections.singletonList(RestTopics.REQUEST));
            logger.info("Listening on topic = {}", RestTopics.REQUEST);
            try {
                for (; ; ) {
                    ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, T> record : records) {
                        if (precondition.apply(record.value())) {
                            logger.info("consuming from topic = {}, partition = {}, offset = {}, key = {}, value = {}", record.topic(), record.partition(), record.offset(), record.key(), record.value());
                            producer.send(new ProducerRecord<>(RestTopics.RESPONSE, 0, record.key(), transformation.apply(record.value()))).get();
                            return;
                        }
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                producer.close();
                consumer.close();
            }
        });
    }

    private static <T> Consumer<String, T> createConsumer(EmbeddedKafkaBroker embeddedKafka) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("consumer" + UUID.randomUUID().toString(), "false", embeddedKafka);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        JsonDeserializer<T> tJsonDeserializer = new JsonDeserializer<>();
        tJsonDeserializer.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(), tJsonDeserializer).createConsumer();
    }

    private static <V> Producer<String, V> createProducer(EmbeddedKafkaBroker embeddedKafka) {
        Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
        return new DefaultKafkaProducerFactory<String, V>(senderProps, new StringSerializer(), new JsonSerializer<>()).createProducer();
    }
}
