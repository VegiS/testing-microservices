package de.zalando.onboarding.b;

import de.zalando.onboarding.b.framework.Consumer;
import de.zalando.onboarding.b.framework.ExternalSystemConnector;
import de.zalando.onboarding.b.logic.BusinessLogic;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This is the test that actually verifies that service B conforms to its own
 * contract.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class, Example1KEBTest.TestConfiguration.class})
public class Example1KEBTest {

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration {

        @Bean
        @Primary
        public ExternalSystemConnector externalSystemConnector() {
            return Mockito.mock(ExternalSystemConnector.class);
        }
    }

    @Inject
    ExternalSystemConnector externalSystemConnector;

    @Inject
    Consumer consumer;

    @InjectMocks
    BusinessLogic businessLogic;

    @Test
    public void receivingE1ShouldLeadToExternalCall() {
        // Spring is calling the JDKs threadpoolexecutor under the hood
        // which is executing the task at startup once. so to properly test it
        // you have to either ask for two invocations or reset the mock.
        Mockito.reset(externalSystemConnector);

        consumer.consume();

        verify(externalSystemConnector, times(1)).triggerExternalE1();
    }

}
