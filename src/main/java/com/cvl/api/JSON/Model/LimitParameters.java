package com.cvl.api.JSON.Model;

public class LimitParameters {
    private Integer instances;
    private SlurmParameters slurm;

    public LimitParameters() {}

    public Integer getInstances() {
        return instances;
    }

    public void setInstances(Integer instances) {
        this.instances = instances;
    }

    public SlurmParameters getSlurm() {
        return slurm;
    }

    public void setSlurm(SlurmParameters slurm) {
        this.slurm = slurm;
    }
}
