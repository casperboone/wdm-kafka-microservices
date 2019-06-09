package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.User;
import nl.tudelft.wdm.group1.common.topic.UsersTopics;
import nl.tudelft.wdm.group1.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public Consumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topicPartitions = {
            @TopicPartition(topic = UsersTopics.USER_CREATED, partitions = "#{instance_id}"),
            @TopicPartition(topic = UsersTopics.CREDIT_ADDED, partitions = "#{instance_id}"),
            @TopicPartition(topic = UsersTopics.CREDIT_SUBTRACTED, partitions = "#{instance_id}"),
    })
    public void consume(User user) {
        logger.info("Consuming [{},{},{}] -> {}",
                UsersTopics.USER_CREATED, UsersTopics.CREDIT_ADDED, UsersTopics.CREDIT_SUBTRACTED, user);

        userRepository.save(user);
    }

    @KafkaListener(topicPartitions = {
            @TopicPartition(topic = UsersTopics.USER_DELETED, partitions = "#{instance_id}")
    })
    public void consumeUserDeleted(User user) throws ResourceNotFoundException {
        logger.info("Consuming [{}] -> {}", UsersTopics.USER_DELETED, user);

        userRepository.delete(userRepository.findOrElseThrow(user.getId()));
    }
}
