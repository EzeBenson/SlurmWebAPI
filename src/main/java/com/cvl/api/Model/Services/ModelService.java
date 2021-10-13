package com.cvl.api.Model.Services;

import com.cvl.api.Model.Model;

import java.util.List;

public interface ModelService {
    List<Model> getAll();
    Model getById(int id);
    void syncModels(List<Model> models);
}
