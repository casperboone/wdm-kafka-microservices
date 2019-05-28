package nl.tudelft.wdm.group1.common;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class KafkaErrorResponse {
    private UUID id;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
    private Throwable payload;

    public KafkaErrorResponse() {

    }

    public KafkaErrorResponse(UUID id, Throwable payload) {
        this.id = id;
        this.payload = payload;
    }

    public Throwable getPayload() {
        return payload;
    }

    public UUID getId() {
        return id;
    }
}
