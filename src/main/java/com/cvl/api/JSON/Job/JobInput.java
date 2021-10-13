package com.cvl.api.JSON.Job;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

@JsonSerialize(using = InputSerializer.class)
public class JobInput {

    private List<List<InputItem>> items;
    private boolean batch;

    public JobInput() {}

    public JobInput(List<List<InputItem>> items, boolean batch) {
        this.items = items;
        this.batch = batch;
    }

    public List<List<InputItem>> getItems() {
        return items;
    }

    public void setItems(List<List<InputItem>> items) {
        this.items = items;
    }

    public int size() {
        return this.items.size();
    }

    public boolean isBatch() {
        return this.batch;
    }

}
