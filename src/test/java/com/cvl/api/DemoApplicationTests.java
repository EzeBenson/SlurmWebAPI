package com.cvl.api;

import com.cvl.api.JSON.Model.ModelDescription;
import com.cvl.api.Model.Model;
import com.cvl.api.Model.Services.ModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    private ModelService modelService;

    @Value("${spring.model-directory}")
    String modelDirectory;

    @Autowired
    public DemoApplicationTests(ModelService modelService) {
        this.modelService = modelService;
    }

    @Test
    public void contextLoads() throws IOException {
        File modelFolder = new File(modelDirectory);
        // Get model file
        File[] fs = modelFolder.listFiles();
        if (fs != null) {
            List<Model> models = new ArrayList<>();
            // Traversing files into model objects
            for (File temp : fs) {
                ObjectMapper mapper = new ObjectMapper();
                ModelDescription modelDescription = mapper.readValue(temp, ModelDescription.class);
                if (modelDescription != null) {
                    models.add(new Model(modelDescription, temp.getPath()));
                }
            }
            // Models are not empty, operate the database
            if (models.size() > 0) {
                modelService.syncModels(models);
            }
        }
    }
}
