package nl.tudelft.wdm.group1.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

public class KafkaResponse<T> {
    private UUID id;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private T payload;

    public KafkaResponse() {

    }

    public KafkaResponse(UUID id, T payload) {
        this.id = id;
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

    public UUID getId() {
        return id;
    }
}
