package com.cvl.api.JSON.Job;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.*;
import java.util.stream.Collectors;

@JsonDeserialize(using = OutputDeserializer.class)
public class JobOutput {

    private List<List<OutputItem>> items;

    public JobOutput() {}

    public JobOutput(List<List<OutputItem>> items) {
        this.items = items;
    }

    public List<List<OutputItem>> getItems() {
        return items;
    }

    public void setItems(List<List<OutputItem>> items) {
        this.items = items;
    }

    public int size() {
        return this.items.size();
    }

    public OutputItem get(int i, String name) {
        Optional<OutputItem> output = this.items.get(i).stream().filter(outputItem -> outputItem.getName().equals(name)).findFirst();
        if (output.isPresent()) {
            return output.get();
        } else {
            return null;
        }
    }

    public Object summary() {
        HashMap<String, Object> dict = new HashMap<>();
        int batchSize = this.items.size();

        List<OutputItem> firstItem = this.items.get(0);

        //List<HashMap<String,Object>> outputItemDescriptions =
        Map<String,String> outputItemDescriptions = firstItem.stream().collect(
                Collectors.toMap(OutputItem::getName,outputItem -> {
            if (outputItem.getSource() == null) return "value";
            else return "data";
        }));

        if (batchSize == 1) {
            dict.put("items", outputItemDescriptions);
        } else {
            dict.put("count", batchSize);
            dict.put("items", outputItemDescriptions);
        }
        return dict;
    }

}
