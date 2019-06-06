package nl.tudelft.wdm.group1.stock;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.internals.AbstractPartitionAssignor;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableKafka
public class KafkaConfig {
    private final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Bean
    public Map<String, Object> consumerConfigs() {
        logger.info("Configuring partition assignment strategy");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, PartitionAssignmentStrategy.class);
        return props;
    }
}

class PartitionAssignmentStrategy extends AbstractPartitionAssignor {
    private final Logger logger = LoggerFactory.getLogger(PartitionAssignmentStrategy.class);

    @Override
    public Map<String, List<TopicPartition>> assign(Map<String, Integer> partitionsPerTopic, Map<String, Subscription> subscriptions) {
        int candidatePartition = 0;
        String podName = System.getenv("POD_NAME");
        if (podName != null) {
            String[] splitName = podName.split("-");
            candidatePartition = Integer.parseInt(splitName[splitName.length - 1]);
        }

        final int finalPartition = candidatePartition;

        logger.info("Application chose partition " + finalPartition);

        return subscriptions.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().topics().stream().map(
                                t -> new TopicPartition(t, finalPartition)
                        ).collect(Collectors.toList())));
    }

    @Override
    public String name() {
        return "WDM";
    }
}
