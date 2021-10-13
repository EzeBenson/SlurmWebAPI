package com.cvl.api.Job.Services;

import com.cvl.api.Job.Job;
import com.cvl.api.Job.JobStatus;

import java.util.List;

public interface JobService {
    List<Job> getAll();
    Job getById(int id);
    void add(Job job);
    void update(Job job);
    List<Job> getProcessingJobs();
    List<Job> getPendingJobs();
    void updateJobStatus(int id, JobStatus status);
    Job getOldestPendingJob();
}
