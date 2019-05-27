package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.*;
import nl.tudelft.wdm.group1.common.payload.*;
import nl.tudelft.wdm.group1.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@KafkaListener(topics = RestTopics.REQUEST)
public class Rest {

    private final UserRepository userRepository;
    private KafkaTemplate<String, Object> rest;

    @Autowired
    public Rest(UserRepository userRepository, KafkaTemplate<String, Object> rest) {
        this.userRepository = userRepository;
        this.rest = rest;
        rest.setDefaultTopic(RestTopics.RESPONSE);
    }

    @KafkaHandler
    public void consumeUserCreate(UserCreatePayload payload) {
        User user = new User(payload.getFirstName(), payload.getLastName(), payload.getStreet(), payload.getZip(), payload.getCity());
        userRepository.addOrReplace(user);
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), user, RestStatus.Success));
    }

    @KafkaHandler
    public void consumeUserDelete(UserDeletePayload payload) throws ResourceNotFoundException {
        userRepository.remove(payload.getUserId());
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), null, RestStatus.Success));
    }

    @KafkaHandler
    public void consumeUserGet(UserGetPayload payload) throws ResourceNotFoundException {
        User user = userRepository.find(payload.getUserId());
        rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), user, RestStatus.Success));
    }

    @KafkaHandler
    public void consumeUserCreditAdd(UserCreditAddPayload payload) {
        try {
            User user = userRepository.find(payload.getUserId());
            user.addCredit(payload.getAmount());

            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), user, RestStatus.Success));
        } catch (ResourceNotFoundException | CreditChangeInvalidException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }

    @KafkaHandler
    public void consumeUserCreditSubtract(UserCreditSubtractPayload payload) {
        try {
            User user = userRepository.find(payload.getUserId());
            user.subtractCredit(payload.getAmount());

            rest.sendDefault(new KafkaResponse<>(payload.getRequestId(), user, RestStatus.Success));
        } catch (ResourceNotFoundException | CreditChangeInvalidException | InsufficientCreditException e) {
            rest.sendDefault(new KafkaErrorResponse(payload.getRequestId(), e));
        }
    }
}
