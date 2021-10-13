package com.cvl.api.Model.Services.Impl;

import com.cvl.api.Model.Model;
import com.cvl.api.Model.Services.ModelRepository;
import com.cvl.api.Model.Services.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HibernateModelService implements ModelService {

    private ModelRepository modelRepository;

    @Autowired
    public HibernateModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    @Override
    public List<Model> getAll() {
        return modelRepository.findAll();
    }

    @Override
    public Model getById(int id)
    {
        Optional<Model> model = modelRepository.findById(id);

        if (model.isPresent())
        {
            return model.get();
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void syncModels(List<Model> models) {
        List<Model> dbModels = getAll();

        // Find, link and update existing models
        for (Model model : models) {
            Optional<Model> existingModel = dbModels.stream().filter((modelH -> modelH.getJsonModelName().equals(model.getJsonModelName()))).findFirst();
            if (existingModel.isPresent()) {
                // Sync model IDs
                model.setModelId(existingModel.get().getModelId());
            }
        }
        modelRepository.saveAll(models);

        // Remove deleted models
        List<String> currentNames = models.stream().map(modelH -> modelH.getJsonModelName()).collect(Collectors.toList());
        List<Model> oldDBModels = dbModels.stream().filter(modelH -> !currentNames.contains(modelH.getJsonModelName())).collect(Collectors.toList());
        modelRepository.deleteAll(oldDBModels);
    }
}
