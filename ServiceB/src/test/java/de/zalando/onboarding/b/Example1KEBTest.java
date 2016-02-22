package de.zalando.onboarding.b;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.zalando.onboarding.b.events.E1;
import de.zalando.onboarding.b.framework.*;
import de.zalando.onboarding.b.keb.ExampleProvider;
import de.zalando.onboarding.b.logic.BusinessLogic;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static de.zalando.onboarding.b.keb.ExampleProvider.Versions.ALL;
import static junitparams.JUnitParamsRunner.$;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
    @Parameters
    public void verifyE1(String input) throws IOException {
        // Spring is calling the JDKs threadpoolexecutor under the hood
        // which is executing the task at startup once. so to properly test it
        // you have to either ask for two invocations or reset the mock.
        // TODO: as soon as the messages that are received are parameterized by the contract input
        // this should be obsolete (assuming that we are doing more than just counting invocations)
        reset(externalSystemConnector);

        List<ConsumerRecord<String, String>> consumerRecords = new ArrayList<>();
        consumerRecords.add(new ConsumerRecord<String, String>("test", 1, 0, "randomKey", input));

        Map<TopicPartition, List<ConsumerRecord<String, String>>> map = new HashMap<>();
        map.put(new TopicPartition("test", 1), consumerRecords);
        ConsumerRecords<String, String> records = new ConsumerRecords<>(map);

        when(kafkaConsumer.poll(anyLong())).thenReturn(records);

        consumer.consume();

        verify(externalSystemConnector, times(1)).triggerExternalE1();
    }

    public Object[] parametersForVerifyE1() throws IOException {

        ExampleProvider exampleProvider = new ExampleProvider(new File("testData"));


        List<String> examples = exampleProvider.getExamples("E1", "ServiceB", ALL);

        Object[] params = new Object[examples.size()];

        /*for (int i = 0; i < examples.size(); i++) {
            ArrayList<ExampleHolder> example = new ArrayList<>();
            example.add(examples.get(i));
            params[i] = new Object[] {example};
        }*/

        for (int i = 0; i < examples.size(); i++) {
            params[i] = new Object[] {examples.get(i)};
        }

        return params;
    }

}
