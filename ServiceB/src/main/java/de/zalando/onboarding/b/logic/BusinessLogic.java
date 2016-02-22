package de.zalando.onboarding.b.logic;

import de.zalando.onboarding.b.events.E1;
import de.zalando.onboarding.b.framework.ExternalSystemConnector;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * This would be the class where you store all your busines logic/microservice state
 * etc. For the sake of the demo it is pretty simple.
 */
@Component
public class BusinessLogic {

    private final ExternalSystemConnector connector;

    @Inject
    public BusinessLogic(ExternalSystemConnector connector) {
        this.connector = connector;
    }

    public void consume(E1 event) {
        // This has to be tested with a dedicated unit test
        if (event != null) {
            connector.triggerExternalE1();
        }
    }
}
