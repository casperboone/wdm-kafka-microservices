package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.User;
import nl.tudelft.wdm.group1.common.topic.UsersTopics;
import nl.tudelft.wdm.group1.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Consumer {
    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public Consumer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = {UsersTopics.USER_CREATED, UsersTopics.CREDIT_ADDED, UsersTopics.CREDIT_SUBTRACTED})
    public void consume(User user) {
        logger.info("Consuming [{},{},{}] -> {}",
                UsersTopics.USER_CREATED, UsersTopics.CREDIT_ADDED, UsersTopics.CREDIT_SUBTRACTED, user);

        userRepository.save(user);
    }

    @KafkaListener(topics = UsersTopics.USER_DELETED)
    public void consumeUserDeleted(User user) throws ResourceNotFoundException {
        logger.info("Consuming [{}] -> {}", UsersTopics.USER_DELETED, user);

        userRepository.delete(userRepository.findOrElseThrow(user.getId()));
    }
}
