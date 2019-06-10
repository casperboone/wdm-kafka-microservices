package nl.tudelft.wdm.group1.rest.events;

import nl.tudelft.wdm.group1.common.KafkaErrorResponse;
import nl.tudelft.wdm.group1.common.KafkaResponse;
import nl.tudelft.wdm.group1.common.payload.RestPayload;
import nl.tudelft.wdm.group1.common.topic.RestTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@KafkaListener(topics = RestTopics.RESPONSE)
public class KafkaInteraction<T> {
    private static final Logger logger = LoggerFactory.getLogger(KafkaInteraction.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<UUID, CompletableFuture<T>> outstanding = new HashMap<>();

    @Value("#{partition}")
    private AtomicInteger partition;

    @Autowired
    public KafkaInteraction(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaHandler
    public void listenKafkaResponse(@Payload KafkaResponse<T> response) {
        if (outstanding.containsKey(response.getId())) {
            logger.info("Successful response = {} message = {}", response.getId(), response.getPayload());
            outstanding.get(response.getId()).complete(response.getPayload());
        }
    }

    @KafkaHandler
    public void listenKafkaErrorResponse(@Payload KafkaErrorResponse response) {
        if (outstanding.containsKey(response.getId())) {
            logger.info("Error in response = {} message = {}", response.getId(), response.getPayload().getMessage());
            outstanding.get(response.getId()).completeExceptionally(response.getPayload());
        }
    }

    public CompletableFuture<T> performAction(String topic, RestPayload payload) {
        return performAction(topic, null, payload);
    }

    public CompletableFuture<T> performAction(String topic, String key, RestPayload payload) {
        UUID requestId = UUID.randomUUID();
        payload.setRequestId(requestId);
        payload.setPartition(partition.get());

        logger.debug("Sending payload from partition {} with key {}", partition.get(), key);

        CompletableFuture<T> future = new CompletableFuture<>();
        outstanding.put(requestId, future);
        this.kafkaTemplate.send(topic, key, payload);

        return future;
    }

}
