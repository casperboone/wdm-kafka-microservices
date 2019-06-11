package nl.tudelft.wdm.group1.rest;

import nl.tudelft.wdm.group1.common.topic.RestTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@org.springframework.context.annotation.Configuration
@EnableKafka
public class Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    @Bean(name = "partition")
    public AtomicInteger getPartition() {
        return myPartition;
    }

    private AtomicInteger myPartition = new AtomicInteger(0);

    @Bean
    public ConsumerAwareRebalanceListener rebalanceListener() {
        return new ConsumerAwareRebalanceListener() {
            @Override
            public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
                logger.info("Got partitions {}", partitions);
                Optional<TopicPartition> partition = partitions.stream().filter(p-> p.topic().equals(RestTopics.RESPONSE)).findFirst();

                partition.ifPresent(topicPartition -> myPartition.set(topicPartition.partition()));
            }
        };
    }

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapAddress;

    @Value("${spring.kafka.consumer.group-id")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory
                = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setConsumerRebalanceListener(rebalanceListener());

        return factory;
    }

    @Bean
    public NewTopic responseTopic() {
        return new NewTopic(RestTopics.RESPONSE, getNumPartitions(), (short) 1);
    }

    private int getNumPartitions() {
        int num = 1;
        String environmentValue = System.getenv("NUM_PARTITIONS");
        if (environmentValue != null) {
            num = Integer.parseInt(environmentValue);
        }
        return num;
    }
}
