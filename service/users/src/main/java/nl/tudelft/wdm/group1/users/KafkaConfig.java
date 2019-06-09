package nl.tudelft.wdm.group1.users;

import nl.tudelft.wdm.group1.common.topic.PaymentsTopics;
import nl.tudelft.wdm.group1.common.topic.UsersTopics;
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
    public NewTopic userCreated() {
        return new NewTopic(UsersTopics.USER_CREATED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic userDeleted() {
        return new NewTopic(UsersTopics.USER_DELETED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic creditAdded() {
        return new NewTopic(UsersTopics.CREDIT_ADDED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic creditSubtracted() {
        return new NewTopic(UsersTopics.CREDIT_SUBTRACTED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic creditSubtractedForPaymentSuccessful() {
        return new NewTopic(UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_SUCCESSFUL, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic creditSubtractedForPaymentFailed() {
        return new NewTopic(UsersTopics.CREDIT_SUBTRACTED_FOR_PAYMENT_FAILED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic paymentCreatedFailed() {
        return new NewTopic(PaymentsTopics.PAYMENT_CREATED, getNumPartitions(), (short) 1);
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
