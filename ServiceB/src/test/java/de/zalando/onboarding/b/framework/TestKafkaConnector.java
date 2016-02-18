package de.zalando.onboarding.b.framework;

import de.zalando.onboarding.b.events.E1;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Primary
public class TestKafkaConnector extends KafkaConnector {

    @Override
    public List<E1> poll() {
        ArrayList<E1> events = new ArrayList<>();
        events.add(new E1());
        return events;
    }
}
