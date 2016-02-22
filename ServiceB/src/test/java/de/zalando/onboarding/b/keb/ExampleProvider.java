package de.zalando.onboarding.b.keb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

public class ExampleProvider {

    public enum Versions {
        ALL
    }

    public ExampleProvider(File sourceDir) {
        this.sourceDir = sourceDir;
    }

    private final File sourceDir;

    public List<String> getExamples(String eventName, String componentName, Versions version) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        List<File> sources = collectSources(eventName, componentName, version);
        List<String> results = new ArrayList<>();

        for (File source : sources) {
            try {
                List<ObjectNode> nodes = mapper.readValue(source, new TypeReference<List<ObjectNode>>() {});
                List<String> strings = nodes.stream().map(ObjectNode::toString).collect(toList());
                results.addAll(strings);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return results;
    }

    private List<File> collectSources(String eventName, String componentName, Versions version) {

        File[] files = sourceDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // TODO: version != ALL has to be implemented
                return name.startsWith(eventName) && name.contains("-" + componentName + "-");
            }
        });

        return new ArrayList<File>(asList(files));
    }
}
