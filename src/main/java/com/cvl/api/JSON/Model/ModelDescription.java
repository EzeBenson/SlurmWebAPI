package com.cvl.api.JSON.Model;

import java.util.List;

public class ModelDescription {
    private String description;
    private String name;

    private List<ModelIO> inputs;
    private List<ModelIO> outputs;

    private RuntimeParameters runtime;

    public ModelDescription() {}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ModelIO> getInputs() {
        return inputs;
    }

    public void setInputs(List<ModelIO> inputs) {
        this.inputs = inputs;
    }

    public List<ModelIO> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ModelIO> outputs) {
        this.outputs = outputs;
    }

    public RuntimeParameters getRuntime() {
        return runtime;
    }

    public void setRuntime(RuntimeParameters runtime) {
        this.runtime = runtime;
    }
}
