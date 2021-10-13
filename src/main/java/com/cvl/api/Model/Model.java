package com.cvl.api.Model;

import com.cvl.api.JSON.Model.ModelDescription;

import javax.persistence.*;

@Entity
@Table(name="model")
public class Model {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer modelId;

    @Column(name="name", unique=true)
    private String jsonModelName;

    @Column(name="path")
    private String jsonPath;

    @Column(name="description")
    private String jsonModelDescription;

    public Model() {}

    public Model(ModelDescription input, String filePath)
    {
        this.modelId = null;
        this.jsonPath = filePath;
        this.jsonModelDescription = input.getDescription();
        this.jsonModelName = input.getName();
    }
    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public String getJsonPath() {
        return jsonPath;
    }

    public void setJsonPath(String jsonPath) {
        this.jsonPath = jsonPath;
    }

    public String getJsonModelName() {
        return jsonModelName;
    }

    public void setJsonModelName(String jsonModelName) {
        this.jsonModelName = jsonModelName;
    }

    public String getJsonModelDescription() {
        return jsonModelDescription;
    }

    public void setJsonModelDescription(String jsonModelDescription) {
        this.jsonModelDescription = jsonModelDescription;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Model)) return false;

        return this.getJsonModelName().equals(((Model) other).getJsonModelName());
    }

    @Override
    public String toString() {
        return "Model{" +
                ", modelId=" + modelId +
                ", jsonDescriptor=" + jsonPath +
                ", jsonModelName=" + jsonModelName +
                ", jsonModelDescription=" + jsonModelDescription +
                "}";
    }
}
