package com.cvl.api.Job.Services.Impl;

import com.cvl.api.Job.Job;
import com.cvl.api.Job.Services.JobRepository;
import com.cvl.api.Job.JobStatus;
import com.cvl.api.Job.Services.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HibernateJobService implements JobService {

    private JobRepository jobRepository;

    @Autowired
    public HibernateJobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Job> getAll() {
        return jobRepository.findAll();
    }

    @Override
    public Job getById(int id) {
        Optional<Job> job = jobRepository.findById(id);

        if (job.isPresent())
        {
            return job.get();
        } else {
            return null;
        }

    }

    @Override
    public void add(Job job) {
        if (job.getJobId() != null) {
            // Existing job
            if (jobRepository.existsById(job.getJobId()))
            {
                // Should throw or return error here
            } else {
                jobRepository.save(job);
            }
        } else
        {
            jobRepository.save(job);
        }

    }

    @Override
    public void update(Job job) {
        if (job.getJobId() != null) {
            if (jobRepository.existsById(job.getJobId())) {
                // Job already exists, update
                jobRepository.save(job);
            } else {
                // Job not found - cannot update
                // Throw or raise error
                return;
            }

        } else {
            // Throw or raise error
            return;
        }

    }

    @Override
    public List<Job> getProcessingJobs() {
        return jobRepository.findJobHByState(JobStatus.PROCESSING);
    }

    @Override
    public List<Job> getPendingJobs() {
        return jobRepository.findJobHByState(JobStatus.PENDING);
    }

    @Override
    public void updateJobStatus(int id, JobStatus status) {
        Optional<Job> selectedJob = jobRepository.findById(id);
        selectedJob.ifPresent(job -> job.setState(status));
    }

    @Override
    public Job getOldestPendingJob() {
        return jobRepository.findFirstByStateOrderByJobIdAsc(JobStatus.PENDING);
    }
}