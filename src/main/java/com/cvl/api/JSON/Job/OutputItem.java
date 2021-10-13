package com.cvl.api.JSON.Job;

public class OutputItem {
    private String name;
    private String source;
    private Double value;

    public OutputItem() {}

    public OutputItem(String name, String source, Double value) {
        this.name = name;
        this.source = source;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
}
