package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.KafkaErrorResponse;
import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.exception.CreditChangeInvalidException;
import nl.tudelft.wdm.group1.common.exception.InsufficientCreditException;
import nl.tudelft.wdm.group1.common.exception.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.model.User;
import nl.tudelft.wdm.group1.common.payload.*;
import nl.tudelft.wdm.group1.common.topic.RestTopics;
import nl.tudelft.wdm.group1.users.KafkaConfig;
import nl.tudelft.wdm.group1.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(topicPartitions = {
        @TopicPartition(topic = RestTopics.USERS_REQUEST, partitions = "#{instance_id}")
})
public class Rest {

    private final UserRepository userRepository;
    private KafkaTemplate<String, Object> rest;

    private final Logger logger = LoggerFactory.getLogger(Rest.class);

    @Autowired
    public Rest(UserRepository userRepository, KafkaTemplate<String, Object> rest) {
        this.userRepository = userRepository;
        this.rest = rest;
        rest.setDefaultTopic(RestTopics.RESPONSE);
    }

    @KafkaHandler
    public void consumeUserCreate(UserCreatePayload payload) {
        User user = new User(
                payload.getId(),
                payload.getFirstName(),
                payload.getLastName(),
                payload.getStreet(),
                payload.getZip(),
                payload.getCity()
        );
        logger.info("Creating user {}", payload.getId());
        userRepository.save(user);
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), user));
    }

    @KafkaHandler
    public void consumeUserDelete(UserDeletePayload payload) {
        userRepository.deleteById(payload.getUserId());
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), null));
    }

    @KafkaHandler
    public void consumeUserGet(UserGetPayload payload) {
        try {
            User user = userRepository.findOrElseThrow(payload.getUserId());
            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), user));
        } catch (ResourceNotFoundException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeUserCreditAdd(UserCreditAddPayload payload) {
        logger.info("Add credit {}", payload.getUserId());
        try {
            User user = userRepository.findOrElseThrow(payload.getUserId());
            user.addCredit(payload.getAmount());
            userRepository.save(user);

            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), user));
        } catch (ResourceNotFoundException | CreditChangeInvalidException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeUserCreditSubtract(UserCreditSubtractPayload payload) {
        try {
            User user = userRepository.findOrElseThrow(payload.getUserId());
            user.subtractCredit(payload.getAmount());
            userRepository.save(user);

            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), user));
        } catch (ResourceNotFoundException | CreditChangeInvalidException | InsufficientCreditException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler(isDefault = true)
    public void listenDefault(Object object) {
    }
}
