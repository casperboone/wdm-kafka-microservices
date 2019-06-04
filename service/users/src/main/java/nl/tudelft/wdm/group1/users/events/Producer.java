package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.model.User;
import nl.tudelft.wdm.group1.common.topics.UsersTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {
    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, User> kafkaTemplate;

    public void emitUserCreated(User user) {
        logger.info("Producing [{}] -> {}", UsersTopics.USER_CREATED, user);
        this.kafkaTemplate.send(UsersTopics.USER_CREATED, user);
    }

    public void emitUserDeleted(User user) {
        logger.info("Producing [{}] -> {}", UsersTopics.USER_DELETED, user);
        this.kafkaTemplate.send(UsersTopics.USER_DELETED, user);
    }

    public void emitCreditSubtracted(User user) {
        logger.info("Producing [{}] -> {}", UsersTopics.CREDIT_SUBTRACTED, user);
        this.kafkaTemplate.send(UsersTopics.CREDIT_SUBTRACTED, user);
    }

    public void emitCreditAdded(User user) {
        logger.info("Producing [{}] -> {}", UsersTopics.CREDIT_ADDED, user);
        this.kafkaTemplate.send(UsersTopics.CREDIT_ADDED, user);
    }
}
