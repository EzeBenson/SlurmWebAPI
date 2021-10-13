package com.cvl.api.JSON.Model;

public class RuntimeParameters {
    private boolean batch_supported;
    private String exec;
    private LimitParameters limits;

    public RuntimeParameters() {}

    public boolean isBatch_supported() {
        return batch_supported;
    }

    public void setBatch_supported(boolean batch_supported) {
        this.batch_supported = batch_supported;
    }

    public String getExec() {
        return exec;
    }

    public void setExec(String exec) {
        this.exec = exec;
    }

    public LimitParameters getLimits() {
        return limits;
    }

    public void setLimits(LimitParameters limits) {
        this.limits = limits;
    }
}
