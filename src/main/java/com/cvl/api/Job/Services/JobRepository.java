package com.cvl.api.Job.Services;

import com.cvl.api.Job.Job;
import com.cvl.api.Job.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Integer> {
    List<Job> findJobHByState(JobStatus state);
    Job findFirstByStateOrderByJobIdAsc(JobStatus state);
}
