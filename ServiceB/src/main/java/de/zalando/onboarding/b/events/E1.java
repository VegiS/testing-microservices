package de.zalando.onboarding.b.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * This is the actual event definition class that you are receiving from the kafka stream
 */
public class E1 {

    private final UUID id;

    @JsonCreator
    public E1(@JsonProperty("id") UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
