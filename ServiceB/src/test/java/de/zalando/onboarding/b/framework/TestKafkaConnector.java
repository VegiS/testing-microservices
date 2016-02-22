package de.zalando.onboarding.b.framework;

import de.zalando.onboarding.b.events.E1;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class TestKafkaConnector extends KafkaConnector {

    private final List<E1> result = new ArrayList<>();

    @Inject
    public TestKafkaConnector(KafkaConsumer<String, String> consumer) {
        super(consumer);
    }

    @Override
    public List<E1> poll() {
        ArrayList<E1> events = new ArrayList<>();
        events.addAll(result);
        return events;
    }

    public void setResult(List<E1> input) {
        result.clear();
        result.addAll(input);
    }
}
