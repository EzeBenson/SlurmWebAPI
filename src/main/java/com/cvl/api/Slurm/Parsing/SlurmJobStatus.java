package com.cvl.api.Slurm.Parsing;

public class SlurmJobStatus {
    private Integer jobId;
    private String status;
    private Integer returnValue;

    public SlurmJobStatus(Integer jobId, String status, Integer returnValue) {
        this.jobId = jobId;
        this.status = status;
        this.returnValue = returnValue;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Integer returnValue) {
        this.returnValue = returnValue;
    }
}
