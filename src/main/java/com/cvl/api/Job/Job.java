package com.cvl.api.Job;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Integer jobId;

    @Column(name="model_id")
    private Integer modelId;

    @Enumerated(EnumType.STRING)
    private JobStatus state;

    @Column(name="creation_data")
    private Date creationDate;

    @Column(name="slurm_id")
    private Integer slurmId;

    public Job() {}

    public Job(Integer jobId, Integer modelId, JobStatus state, Date creationDate)
    {
        this.jobId = jobId;
        this.modelId = modelId;
        this.state = state;
        this.creationDate = creationDate;
    }
    public Job(Integer jobId, Integer modelId, JobStatus state, Date creationDate, Integer slurmid)
    {
        this.jobId = jobId;
        this.modelId = modelId;
        this.state = state;
        this.creationDate = creationDate;
        this.slurmId = slurmid;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public JobStatus getState() {
        return state;
    }

    public void setState(JobStatus state) {
        this.state = state;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getSlurmId() {
        return slurmId;
    }

    public void setSlurmId(Integer slurmId) {
        this.slurmId = slurmId;
    }


    @Override
    public String toString() {
        return "Job{" +
                ", jobId=" + jobId +
                ", modelId=" + modelId +
                ", state=" + state +
                ", creationDate=" + creationDate +
                "}";
    }
}
