package de.zalando.onboarding.b.framework;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public class ExampleHolder {

    private final UUID key;
    private final JsonNode value;

    @JsonCreator
    public ExampleHolder(@JsonProperty("key") UUID key,
                         @JsonProperty("value") JsonNode value) {
        this.key = key;
        this.value = value;
    }

    public UUID getKey() {
        return key;
    }

    public String getValue() {
        return value == null ? null : value.toString();
    }
}
