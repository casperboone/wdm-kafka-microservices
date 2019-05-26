package nl.tudelft.wdm.group1.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.UUID;

public class KafkaResponse<T> {
    private UUID id;
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="@class")
    private T payload;
    private RestStatus status;

    public KafkaResponse() {

    }

    public KafkaResponse(UUID id, T payload, RestStatus status) {
        this.id = id;
        this.payload = payload;
        this.status = status;
    }

    public T getPayload() {
        return payload;
    }

    public UUID getId() {
        return id;
    }

    public RestStatus getStatus() {
        return status;
    }
}
