package com.cvl.api.JSON.Job;

import com.cvl.api.JSON.Job.JobInput;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InputSerializer extends StdSerializer<JobInput> {

    public InputSerializer() {
        this(null);
    }

    public InputSerializer(Class<JobInput> t) {
        super(t);
    }

    @Override
    public void serialize(JobInput value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {

        ObjectMapper om = new ObjectMapper();

        Map<String, Object> rootMap = new HashMap<>();

        boolean isBatch = value.isBatch();
        if (isBatch || value.getItems().size() > 1) {
            rootMap.put("input", value.getItems());
        } else {
            rootMap.put("input", value.getItems().get(0));
        }

        JsonNode node = om.valueToTree(rootMap);
        jgen.writeTree(node);
    }
}