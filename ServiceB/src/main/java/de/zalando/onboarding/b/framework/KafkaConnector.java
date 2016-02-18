package de.zalando.onboarding.b.framework;

import de.zalando.onboarding.b.events.E1;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KafkaConnector {

    public List<E1> poll() {

        // TODO: This is the point where you should do your actual kafka connection.
        // For the sake of the demo we don't need it so there is nothing here.

        throw new IllegalStateException("You really should implement this...");
    }
}
