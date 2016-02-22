package de.zalando.onboarding.b.logic;

import de.zalando.onboarding.b.events.E1;
import de.zalando.onboarding.b.framework.ExternalSystemConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * This would be the class where you store all your busines logic/microservice state
 * etc. For the sake of the demo it is pretty simple.
 */
@Component
public class BusinessLogic {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);

    private final ExternalSystemConnector connector;

    @Inject
    public BusinessLogic(ExternalSystemConnector connector) {
        this.connector = connector;
    }

    public void consume(E1 event) {
        // This has to be tested with a dedicated unit test
        if (event != null) {
            LOG.info("Business logic is consuming event with id [{}].", event.getId());
            connector.triggerExternalE1();
        } else {
            LOG.warn("Received consume call with [NULL] event.");
        }
    }
}
