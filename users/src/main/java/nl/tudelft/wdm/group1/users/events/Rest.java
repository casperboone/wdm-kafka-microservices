package nl.tudelft.wdm.group1.users.events;

import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.ResourceNotFoundException;
import nl.tudelft.wdm.group1.common.RestStatus;
import nl.tudelft.wdm.group1.common.RestTopics;
import nl.tudelft.wdm.group1.common.User;
import nl.tudelft.wdm.group1.common.payload.UserCreatePayload;
import nl.tudelft.wdm.group1.common.payload.UserDeletePayload;
import nl.tudelft.wdm.group1.common.payload.UserGetPayload;
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
    private KafkaTemplate<String, KafkaResponse> rest;

    @Autowired
    public Rest(UserRepository userRepository, KafkaTemplate<String, KafkaResponse> rest) {
        this.userRepository = userRepository;
        this.rest = rest;
    }

    @KafkaHandler
    public void consumeUserCreate(UserCreatePayload payload) {
        User user = new User(payload.getFirstName(), payload.getLastName(), payload.getStreet(), payload.getZip(), payload.getCity());

        userRepository.addOrReplace(user);

        rest.send(RestTopics.RESPONSE, new KafkaResponse<>(payload.getRequestId(), user, RestStatus.Success));
    }

    @KafkaHandler
    public void consumeUserDelete(UserDeletePayload payload) throws ResourceNotFoundException {
        userRepository.remove(payload.getUserId());

        rest.send(RestTopics.RESPONSE, new KafkaResponse<>(payload.getRequestId(), null, RestStatus.Success));
    }

    @KafkaHandler
    public void consumeUserGet(UserGetPayload payload) throws ResourceNotFoundException {
        rest.send(RestTopics.RESPONSE, new KafkaResponse<>(payload.getRequestId(), userRepository.find(payload.getUserId()), RestStatus.Success));
    }
}
