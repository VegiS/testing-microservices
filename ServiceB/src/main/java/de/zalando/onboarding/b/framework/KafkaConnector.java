package de.zalando.onboarding.b.framework;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.zalando.onboarding.b.events.E1;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class KafkaConnector {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConnector.class);

    private final KafkaConsumer<String, String> consumer;
    private final ObjectMapper mapper;;

    @Inject
    public KafkaConnector(KafkaConsumer<String, String> consumer) {
        this.consumer = consumer;

        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    }

    // This is the point where you should do your actual kafka connection.
    // In this demo the important parts will be mocked away in the tests.
    public List<E1> poll() throws IOException {

        LOG.info("Polling...");

        ConsumerRecords<String, String> records = consumer.poll(100);


        List<E1> events = new ArrayList<>();

        // Thanks to mocking and multithreading this can actually happen
        if (records != null) {
            LOG.info("Polled[{}] records from the queue.", records.count());
            for (ConsumerRecord<String, String> record : records) {
                String value = record.value();
                LOG.debug("Payload json is [{}].", value);
                events.add(mapper.readValue(value, E1.class));
            }
        } else {
            LOG.info("Got NULL result from queue poll!");
        }

        return events;
    }
}
