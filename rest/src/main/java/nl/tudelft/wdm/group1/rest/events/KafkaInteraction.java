package nl.tudelft.wdm.group1.rest.events;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.RestStatus;
import nl.tudelft.wdm.group1.common.RestTopics;
import nl.tudelft.wdm.group1.common.User;
import nl.tudelft.wdm.group1.common.payload.RestPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaInteraction<T> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaInteraction.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<UUID, CompletableFuture<T>> outstanding = new HashMap<>();

    @Autowired
    public KafkaInteraction(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "response")
    public void listenKafkaResponse(@Payload KafkaResponse<T> response) {
        if (outstanding.containsKey(response.getId())) {
            logger.info("response = {} status = {}", response.getPayload(), response.getStatus());
            if (response.getStatus().equals(RestStatus.Success)) {
                logger.info("success on request {}", response.getId());
                outstanding.get(response.getId()).complete(response.getPayload());
            } else {
                logger.info("failure on request {}", response.getId());
                outstanding.get(response.getId()).completeExceptionally(new Exception());
            }
        }
    }

    public CompletableFuture<T> performAction(RestPayload payload) {
        UUID requestId = UUID.randomUUID();
        payload.setRequestId(requestId);

        CompletableFuture<T> future = new CompletableFuture<>();
        outstanding.put(requestId, future);
        this.kafkaTemplate.send(RestTopics.REQUEST, payload);

        return future;
    }

}
