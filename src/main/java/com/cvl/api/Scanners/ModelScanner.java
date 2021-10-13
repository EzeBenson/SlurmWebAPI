package com.cvl.api.Scanners;

import com.cvl.api.JSON.Model.ModelDescription;
import com.cvl.api.Model.Services.ModelService;
import com.cvl.api.Model.Model;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ModelScanner {
    private ModelService modelService;

    @Value("${storage.model-directory}")
    String modelDirectory;

    @Autowired
    public ModelScanner(ModelService modelService) {
        this.modelService = modelService;
    }

    @Scheduled(fixedDelayString = "${scanners.model-refresh-frequency}")
    public void loadModel() throws IOException {
        File modelFolder = new File(modelDirectory);
        File[] fs = modelFolder.listFiles();
        if (fs != null) {
            List<Model> models = new ArrayList<>();
            // Files into model objects
            for (File temp : fs) {
                if (!temp.isDirectory() && temp.getName().endsWith(".json")) {
                    ObjectMapper mapper = new ObjectMapper();
                    ModelDescription modelDescription = mapper.readValue(temp, ModelDescription.class);
                    if (modelDescription != null) {
                        models.add(new Model(modelDescription, temp.getPath()));
                    }
                }
            }
            // If models are not empty, access the database
            if (models.size() > 0) {
                modelService.syncModels(models);
            }
        }

    }


}

