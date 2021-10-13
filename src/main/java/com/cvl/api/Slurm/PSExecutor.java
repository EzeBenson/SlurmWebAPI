package com.cvl.api.Slurm;

import com.cvl.api.JSON.Model.RuntimeParameters;
import com.cvl.api.Slurm.Parsing.SlurmJobStatus;

import java.util.HashSet;

public interface PSExecutor {
    public HashSet<Integer> getCurrentJobs();
    public SlurmJobStatus getJobStatus(int jobid);
    public Integer executeJob(RuntimeParameters parameters, String inputJsonPath, String outputFolder);
}

