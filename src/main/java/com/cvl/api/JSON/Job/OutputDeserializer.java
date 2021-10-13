package com.cvl.api.JSON.Job;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OutputDeserializer extends StdDeserializer<JobOutput> {
    public OutputDeserializer() {
        this(null);
    }

    public OutputDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JobOutput deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        JsonNode root = node.get("outputs");

        // Inspect first child element to determine if it is likely to be a list, or an OutputItem
        boolean batch = !root.get(0).isObject();

        if (batch) {
            List<List<OutputItem>> nestedItems = new ArrayList<>();
            for (JsonNode child : root) {
                List<OutputItem> items = new ArrayList<>();
                for (JsonNode elem : child) {
                    items.add(jp.getCodec().treeToValue(elem, OutputItem.class));
                }
                nestedItems.add(items);
            }
            return new JobOutput(nestedItems);
        } else {
            List<OutputItem> items = new ArrayList<>();
            for (JsonNode child : root) {
                items.add(jp.getCodec().treeToValue(child, OutputItem.class));
            }
            List<List<OutputItem>> nestedItems = new ArrayList<>();
            nestedItems.add(items);
            return new JobOutput(nestedItems);
        }
    }
}