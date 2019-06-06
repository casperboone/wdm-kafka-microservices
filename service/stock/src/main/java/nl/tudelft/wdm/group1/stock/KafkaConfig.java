package nl.tudelft.wdm.group1.stock;

import nl.tudelft.wdm.group1.common.StockTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

import java.util.HashMap;
import java.util.Map;

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

    @Bean
    public NewTopic stockCreated() {
        return new NewTopic(StockTopics.STOCK_ITEM_CREATED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic stockAdded() {
        return new NewTopic(StockTopics.STOCK_ADDED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic stockSubtracted() {
        return new NewTopic(StockTopics.STOCK_SUBTRACTED, getNumPartitions(), (short) 1);
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
