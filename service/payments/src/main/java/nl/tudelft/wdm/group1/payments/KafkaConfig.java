package nl.tudelft.wdm.group1.payments;

import nl.tudelft.wdm.group1.common.topic.PaymentsTopics;
import nl.tudelft.wdm.group1.common.topic.RestTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaConfig {
    private final Logger logger = LoggerFactory.getLogger(KafkaConfig.class);

    @Bean(name = "instance_id")
    public String instanceId() {
        String podName = System.getenv("POD_NAME");
        if (podName != null) {
            return podName.substring(1 + podName.lastIndexOf('-'));
        }
        return "0";
    }

    @Bean
    public NewTopic paymentCreated() {
        return new NewTopic(PaymentsTopics.PAYMENT_CREATED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic paymentFailed() {
        return new NewTopic(PaymentsTopics.PAYMENT_FAILED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic paymentDeleted() {
        return new NewTopic(PaymentsTopics.PAYMENT_DELETED, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic paymentSuccess() {
        return new NewTopic(PaymentsTopics.PAYMENT_SUCCESSFUL, getNumPartitions(), (short) 1);
    }

    @Bean
    public NewTopic paymentsRequest() {
        return new NewTopic(RestTopics.PAYMENTS_REQUEST, getNumPartitions(), (short) 1);
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
