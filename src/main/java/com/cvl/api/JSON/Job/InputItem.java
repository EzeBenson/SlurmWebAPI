package com.cvl.api.JSON.Job;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("input")
public class InputItem {
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String source;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object value;

    public InputItem() {}

    public InputItem(String name, String source, Object value) {
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

    public Object getValue() {
        return value;
    }

    public void setValue(Double doubleValue) {
        this.value = value;
    }

}
