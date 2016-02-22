package de.zalando.onboarding.b.framework;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Providers {

    @Bean
    public KafkaConsumer<String, String> kafkaConsumer() {
        // This is only needed to satisfy spring. During the tests it is mocked away.
        return null;
    }
}
