package de.zalando.onboarding.b.framework;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zalando.onboarding.b.events.E1;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

@Component
public class KafkaConnector {


    private final KafkaConsumer<String, String> consumer;

    @Inject
    public KafkaConnector(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;
    }

    // This is the point where you should do your actual kafka connection.
    // In this demo the important parts will be mocked away in the tests.
    public List<E1> poll() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        ConsumerRecords<String, String> records = consumer.poll(100);
        List<E1> events = new ArrayList<>();

        // Thanks to mocking and multithreading this can actually happen
        if (records != null) {
            for (ConsumerRecord<String, String> record : records) {
                events.add(mapper.readValue(record.value(), E1.class));
            }
        }

        return events;
    }
}
