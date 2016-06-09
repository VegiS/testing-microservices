package de.zalando.onboarding.b;

import de.zalando.onboarding.b.framework.Consumer;
import de.zalando.onboarding.b.framework.ExternalSystemConnector;
import de.zalando.onboarding.b.logic.BusinessLogic;
import de.zalando.toga.provider.ExampleProviderRule;
import junitparams.JUnitParamsRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.regex.Pattern.compile;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * This is the test that actually verifies that service B conforms to its own
 * contract.
 */
@RunWith(JUnitParamsRunner.class)
@ContextConfiguration(classes = {Application.class, Example1KEBTest.TestConfiguration.class})
public class Example1KEBTest {

    @ClassRule
    public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();

    @Rule
    public final SpringMethodRule SPRING_METHOD_RULE = new SpringMethodRule();

    @Rule
    public final ExampleProviderRule examples = new ExampleProviderRule(new File("testData"), compile("E1-ServiceB"));

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration {

        @Bean
        @Primary
        public ExternalSystemConnector externalSystemConnector() {
            return Mockito.mock(ExternalSystemConnector.class);
        }

        @Bean
        @Primary
        public KafkaConsumer<String, String> testConsumer() {
            return Mockito.mock(KafkaConsumer.class);
        }
    }

    @Inject
    ExternalSystemConnector externalSystemConnector;

    @Inject
    Consumer consumer;

    @Inject
    KafkaConsumer<String, String> kafkaConsumer;

    @InjectMocks
    BusinessLogic businessLogic;

    @Test
    public void verifyE1() throws IOException {
        // Spring is calling the JDKs threadpoolexecutor under the hood
        // which is executing the task at startup once. so to properly test it
        // you have to either ask for two invocations or reset the mock.
        // TODO: as soon as the messages that are received are parameterized by the contract input
        // this should be obsolete (assuming that we are doing more than just counting invocations)
        reset(externalSystemConnector);

        List<ConsumerRecord<String, String>> consumerRecords = new ArrayList<>();
        String input = examples.getExampleJson();
        consumerRecords.add(new ConsumerRecord<>("test", 1, 0, "randomKey", input));

        Map<TopicPartition, List<ConsumerRecord<String, String>>> map = new HashMap<>();
        map.put(new TopicPartition("test", 1), consumerRecords);
        ConsumerRecords<String, String> records = new ConsumerRecords<>(map);

        when(kafkaConsumer.poll(anyLong())).thenReturn(records);

        consumer.consume();

        verify(externalSystemConnector, times(1)).triggerExternalE1();
    }
}
