package com.cvl.api.Controllers;


import com.cvl.api.JSON.Job.InputItem;
import com.cvl.api.JSON.Job.JobInput;
import com.cvl.api.JSON.Job.JobOutput;
import com.cvl.api.JSON.Job.OutputItem;
import com.cvl.api.JSON.Model.ModelDescription;
import com.cvl.api.JSON.Model.ModelIO;
import com.cvl.api.Job.Job;
import com.cvl.api.Job.JobStatus;
import com.cvl.api.Model.Model;
import com.cvl.api.Utils.FileNameUtils;
import com.cvl.api.Job.Services.JobService;
import com.cvl.api.Utils.MimeType;
import com.cvl.api.Model.Services.ModelService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * <p>
 * Job Controllers
 * </p>
 *
 * @author Haoguo WU
 */

@RestController
@RequestMapping("/job")
public class JobController {

    private JobService jobService;
    private ModelService modelService;

    @Value("${storage.data-directory}")
    String dataDirectory;

    @Autowired
    public JobController(JobService jobService, ModelService modelService) {
        this.jobService = jobService;
        this.modelService = modelService;
    }

    @GetMapping("")
    public Object list() {
        return jobService.getAll();
    }

    // Returns job status
    @GetMapping(value = "/{job_id}")
    public Object getByJobId(@PathVariable("job_id") Integer job_id) throws IOException {
        Job job = jobService.getById(job_id);

        if (job == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Job not found"
            );
        }
        return job.getState().toString();
    }

    private void throw404(String message) throws ResponseStatusException {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, message
        );
    }

    // Obtains a job from the service, and throws appropriate errors if it is not found or has no output
    private Job getJobForOutput(Integer job_id) {
        Job job = jobService.getById(job_id);

        // If job isn't found, 404
        if (job == null) {
            throw404("Job not found");
        }

        // If job isn't complete, there is no output
        if(job.getState() != JobStatus.COMPLETE)
        {
            throw404("No output, job incomplete");
        }

        return job;
    }

    // Returns job status, or output JSON if complete
    @GetMapping(value = "/{job_id}/output", produces = "application/json")
    public Object getOutputByJobID(@PathVariable("job_id") Integer job_id) throws IOException {
        Job job = getJobForOutput(job_id);

        Path jobsPath = Paths.get(dataDirectory).resolve("jobs").resolve(job.getJobId().toString());
        Path resourcePath = jobsPath.resolve("outputs").resolve("output.json");

        ObjectMapper om = new ObjectMapper();
        JobOutput jobOutput = om.readValue(resourcePath.toFile(), JobOutput.class);

        return jobOutput.summary();
    }

    @GetMapping(value = {"/{job_id}/output/{output_id}/{output_name}","/{job_id}/output/{output_name}"})
    public void getOutput(@PathVariable("job_id") Integer job_id,
                          @PathVariable(value = "output_id", required = false) Integer output_id,
                          @PathVariable("output_name") String name,
                          HttpServletRequest request,
                          HttpServletResponse response) throws IOException {

        // Obtain job information
        Job job = getJobForOutput(job_id);

        // Output directory path
        Path outputPath = Paths.get(dataDirectory).resolve("jobs")
                                                  .resolve(job_id.toString())
                                                  .resolve("outputs");

        Path outputJsonPath = outputPath.resolve("output.json");

        ObjectMapper om = new ObjectMapper();
        JobOutput jobOutput = om.readValue(outputJsonPath.toFile(), JobOutput.class);

        // Map no specified index to index 0
        if (output_id == null) {
            // No output_id specified, only acceptable if this is not a batch output
            if (jobOutput.size() > 1) {
                throw404("Batch output index required");
            }
            output_id = 0;
        }

        // Check index is in range
        if (output_id >= jobOutput.size()) {
            throw404("Batch output index not found");
        }

        OutputItem resource = jobOutput.get(output_id, name);
        if (resource == null) {
            throw404("Output " + name + " not found");
        }

        if (resource.getValue() != null) {
            returnValueToURL(response, resource.getValue());
        } else if (resource.getSource() != null) {
            String source = resource.getSource();
            String resourceName = new File(source).getName();

            Path resourcePath = outputPath.resolve(resourceName);
            if (!resourcePath.toFile().exists()) {
                // Resource should exist, but is not found
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Could not access resource"
                );
            }
            returnFileToURL(response, resourcePath.toFile());
        } else {
            // Resource should exist, but job output has no link to it
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Could not obtain resource data from output JSON"
            );
        }
    }

    // HTTP response of a single value
    private static void returnValueToURL(HttpServletResponse response, Double value) {
        response.setContentType("text/plain;charset=UTF-8");
        try (Writer writer = new OutputStreamWriter(response.getOutputStream(), "UTF-8")) {
            writer.write(value.toString());
        } catch (IOException ex) {
            // Logging potentially required here
            ex.printStackTrace();
        }
    }

    // HTTP response returning a file
    private static void returnFileToURL(HttpServletResponse response, File file) {
        // Infer mime type for header
        String contentType = MimeType.getContentType(file.getName(),"UTF-8");

        // Open streams and send data
        try(BufferedInputStream is = new BufferedInputStream(new FileInputStream(file))) {
            try (BufferedOutputStream os = new BufferedOutputStream(response.getOutputStream())) {
                response.setContentType("image/jpeg");

                /* I don't think setting a content-disposition is necessary, if we want it I recommend returning
                   the resource key, not the file name itself which may be meaningless
                response.setHeader("Content-disposition", "attachment; filename="
                    + new String(file.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
                */

                byte[] buff = new byte[2048];
                int bytesRead;
                while (-1 != (bytesRead = is.read(buff, 0, buff.length))) {
                    os.write(buff, 0, bytesRead);
                }
            } catch (IOException ex) {
                throw ex;
            }
        } catch (IOException ex) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Unable to return data"
            );
        }
    }

    private void throw400(String message) throws ResponseStatusException {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, message
        );
    }

    private void throw500(String message) throws ResponseStatusException {
        throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, message
        );
    }

    // Multi-part upload of files
    @Transactional(rollbackFor = IOException.class)
    @PostMapping (value = "", consumes = {"multipart/form-data"})
    public Object postJob(@RequestParam("model_id") Integer model_id, MultipartHttpServletRequest request) throws IOException {
        // Checks on model_id
        Model model = modelService.getById(model_id);

        if (model == null) {
            throw400("Model ID not found");
        }

        ModelDescription modelDescription = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            modelDescription = mapper.readValue(new File(model.getJsonPath()), ModelDescription.class);

            // Confirm parseFile hasn't returned null
            if (modelDescription == null)
                throw new Exception("");
        } catch (Exception ex) {
            throw500("Could not parse selected model description.");
        }

        Map<String, MultipartFile> fileMap = request.getFileMap();
        Map<String, MultipartFile> processedFileMap = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        List<InputItem> jobInputs = new ArrayList<>();
        boolean batch_support = modelDescription.getRuntime().isBatch_supported();

        for (ModelIO input : modelDescription.getInputs()) {
            switch (input.type.toLowerCase()) {
                case "matrix":
                    if (fileMap.containsKey(input.name)) {
                        MultipartFile file = fileMap.get(input.name);
                        if (file.isEmpty()) {
                            throw400(String.format("Empty file found in place of %s", input.name));
                        }
                        processedFileMap.put(input.name, file);
                    } else {
                        throw400(String.format("Required matrix input %s not provided", input.name));
                    }
                    break;
                case "integer":
                    if (parameterMap.containsKey(input.name)) {
                        try {
                            Integer i = Integer.parseInt(parameterMap.get(input.name)[0]);
                            jobInputs.add(new InputItem(input.name, null, i));
                        } catch (NumberFormatException ex) {
                            throw400(String.format("Could not parse integer input %s", input.name));
                        }
                    } else {
                        throw400(String.format("Required integer input %s not provided", input.name));
                    }
                    break;
                case "float":
                    if (parameterMap.containsKey(input.name)) {
                        try {
                            Double d = Double.parseDouble(parameterMap.get(input.name)[0]);
                            jobInputs.add(new InputItem(input.name, null, d));
                        } catch (NumberFormatException ex) {
                            throw400(String.format("Could not parse float input %s", input.name));
                        }
                    } else {
                        throw400(String.format("Required float input %s not provided", input.name));
                    }
                    break;
                default:
                    throw500(String.format("%s in model description not a valid model input type", input.type));
            }
        }

        // Inputs have been validated, create job
        Job job = new Job(null, model_id, JobStatus.PENDING, new Date());
        jobService.add(job);

        // Obtain new job_id
        Integer job_id = job.getJobId();

        // Create file system and copy new files
        Path jobsPath = Paths.get(dataDirectory).resolve("jobs").resolve(job_id.toString());
        Path inputPath = jobsPath.resolve("inputs");

        //Create folders and transfer files
        try {
            File inputDir = inputPath.toFile();
            if (!inputDir.exists()) {
                inputDir.mkdirs();
            }

            for (Map.Entry<String, MultipartFile> entry : processedFileMap.entrySet()) {
                String key = entry.getKey();
                MultipartFile value = entry.getValue();

                String filename = value.getOriginalFilename();
                String originalFileExt = filename == null ? "tmp" : FileNameUtils.getExtension(filename);
                String newfileName = FileNameUtils.generateRandomFileName(originalFileExt);

                Path dest = inputPath.resolve(newfileName);
                value.transferTo(dest.toFile());

                jobInputs.add(new InputItem(key, dest.toString(), null));
            }

        } catch (Exception ex) {
            throw500("Could not copy received files");
        }

        // Prepare output
        List<List<InputItem>> items = new ArrayList<>();
        items.add(jobInputs);
        JobInput inputs = new JobInput(items, batch_support);

        // Save output.json
        Path jsonPath = inputPath.resolve("input.json");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(jsonPath.toFile(), inputs);

        // Return new job id
        return job_id;
    }

}

