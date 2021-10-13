package com.cvl.api.JSON.Model;

public class SlurmParameters {
    private Integer gres;
    private String qos;
    private String partition;

    public SlurmParameters() {};

    public Integer getGres() {
        return gres;
    }

    public void setGres(Integer gres) {
        this.gres = gres;
    }

    public String getQos() {
        return qos;
    }

    public void setQos(String qos) {
        this.qos = qos;
    }

    public String getPartition() {
        return partition;
    }

    public void setPartition(String partition) {
        this.partition = partition;
    }
}
