package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.users.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Value("${spring.kafka.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;

    public void send(User user) {
        logger.info(String.format("#### -> Producing message -> %s", user));
        this.kafkaTemplate.send(topic, user);
    }
}
