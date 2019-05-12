package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.users.User;
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

    @KafkaListener(topics = "${spring.kafka.topic}")
    public void consume(User user) {
        logger.info(String.format("#### -> Consumed message -> %s", user));

        userRepository.addOrReplace(user);
    }
}
