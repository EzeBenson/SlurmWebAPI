package com.cvl.api.Scanners;

import com.cvl.api.JSON.Model.ModelDescription;
import com.cvl.api.Model.Model;
import com.cvl.api.Slurm.LinuxService;
import com.cvl.api.Slurm.PSExecutor;
import com.cvl.api.Job.Services.JobService;
import com.cvl.api.Model.Services.ModelService;
import com.cvl.api.Job.Job;
import com.cvl.api.Job.JobStatus;
import com.cvl.api.Slurm.Parsing.SlurmJobStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class JobScanner {

    private JobService jobService;
    private ModelService modelService;
    private PSExecutor psexec;

    @Value("${storage.model-directory}")
    String modelDirectory;

    @Value("${storage.data-directory}")
    String dataDirectory;

    @Autowired
    public JobScanner(JobService jobService, ModelService modelService, PSExecutor psexec) {
        this.jobService = jobService;
        this.modelService = modelService;
        this.psexec = psexec;
    }

    @Scheduled(fixedDelayString = "${scanners.completed-job-frequency}")
    public void synchronizeSlurmQueue(){
        List<Job> processingJobs = jobService.getProcessingJobs();
        HashSet<Integer> slurmJobs = psexec.getCurrentJobs();

        for (Job job: processingJobs)
        {
            if(!slurmJobs.contains(job.getSlurmId()))
            {
                //Job has finished or failed
                SlurmJobStatus scontrolOutput = psexec.getJobStatus(job.getSlurmId());

                if (scontrolOutput == null) {
                    // Does not currently implement failure for jobs not found in slurm
                    // Should be logged.
                    continue;
                }

                String slurm_status = scontrolOutput.getStatus();

                switch (slurm_status) {
                    case "RUNNING":
                        jobService.updateJobStatus(job.getJobId(), JobStatus.PROCESSING);
                        break;
                    case "FAILED":
                        jobService.updateJobStatus(job.getJobId(), JobStatus.FAILED);
                        break;
                    case "COMPLETED":
                        jobService.updateJobStatus(job.getJobId(), JobStatus.COMPLETE);
                        break;
                    default:
                        System.out.println("Unexpected job status at jobID " + job.getJobId());
                }
            }
        }
    }

    @Scheduled(fixedDelayString = "${scanners.start-job-frequency}")
    public void grabFromDatabase() {
        List<Job> pendingJobs = jobService.getPendingJobs();

        // For all pending jobs, start them on slurm
        for (Job job : pendingJobs) {
            // Get model
            Model model = modelService.getById(job.getModelId());

            try {
                // Read model json
                ObjectMapper mapper = new ObjectMapper();
                ModelDescription modelDescription = mapper.readValue(new File(model.getJsonPath()), ModelDescription.class);

                Path jobsPath = Paths.get(dataDirectory).resolve("jobs").resolve(job.getJobId().toString());
                String inputJsonPath = jobsPath.resolve("inputs").resolve("input.json").toString();
                String outputPath = jobsPath.resolve("outputs").toString();
                new File(outputPath).mkdirs();

                // Read exec path and convert to absolute
                String execPath = modelDescription.getRuntime().getExec();
                if (!Paths.get(execPath).isAbsolute()) {
                    Path relativePath = Paths.get(model.getJsonPath()).getParent().resolve(execPath);
                    execPath = relativePath.toFile().getCanonicalFile().toString();
                }

                // Assign absolute path back to runtime
                modelDescription.getRuntime().setExec(execPath);

                // Execute job
                Integer slurmJobId = psexec.executeJob(modelDescription.getRuntime(), inputJsonPath, outputPath);

                // Requires verification of jobID
                if (slurmJobId != null) {
                    job.setState(JobStatus.PROCESSING);
                    job.setSlurmId(slurmJobId);
                    jobService.update(job);
                } else {
                    // Job creation failed
                    job.setState(JobStatus.FAILED);
                    jobService.update(job);
                }
            } catch (Exception ex) {
                // Job creation failed
                job.setState(JobStatus.FAILED);
                jobService.update(job);
            }
        }
    }
}

