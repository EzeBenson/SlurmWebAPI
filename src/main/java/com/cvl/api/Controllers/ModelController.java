package com.cvl.api.Controllers;

import com.cvl.api.JSON.Model.ModelDescription;
import com.cvl.api.Model.Model;
import com.cvl.api.Model.Services.ModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 *  Frontend Controller
 * </p>
 *
 * @author Haoguo
 * @since 2019-07-04
 */
@RestController
@RequestMapping("/model")
public class ModelController {

    private ModelService modelService;

    @Autowired
    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping("")
    public Object list() {
        return modelService.getAll();
    }

    //Return required inputs of this model
    @GetMapping("/{model_id}")
    public Object getByModelId(@PathVariable("model_id") Integer model_id) throws IOException {
        Model model = modelService.getById(model_id);

        if (model == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "entity not found"
            );
        }
        File file = new File(model.getJsonPath());
        ObjectMapper mapper = new ObjectMapper();
        ModelDescription modelDescription = mapper.readValue(file, ModelDescription.class);
        return modelDescription;
    }
}