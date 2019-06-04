package nl.tudelft.wdm.group1.orders.events;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

//    @Bean
//    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
//    kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
//                new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.setConcurrency(3);
//        factory.getContainerProperties().setPollTimeout(3000);
//        return factory;
//    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, PartitionStrategy.class);
        return props;
    }
}

class PartitionStrategy extends AbstractPartitionAssignor {

    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic, Map<String, Subscription> subscriptions) {
        return null;
    }

    @Override
    public String name() {
        return null;
    }
}