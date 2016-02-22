package de.zalando.onboarding.b.framework;

import de.zalando.onboarding.b.events.E1;
import de.zalando.onboarding.b.logic.BusinessLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Component
public class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    private final KafkaConnector kafkaConnector;
    private final BusinessLogic businessLogic;

    @Inject
    public Consumer(KafkaConnector kafkaConnector, BusinessLogic businessLogic) {
        this.kafkaConnector = kafkaConnector;
        this.businessLogic = businessLogic;
    }

    @Scheduled(fixedDelay = 3000)
    public void consume() throws IOException {
        List<E1> events = kafkaConnector.poll();
        LOG.info("Consuming [{}] events.", events.size());

        events.forEach(businessLogic::consume);
    }
}
