package com.wzh.suyuan.network.model;

import java.util.List;

public class TraceDetail {
    private TraceBatch batch;
    private List<TraceLogisticsNode> logistics;

    public TraceBatch getBatch() {
        return batch;
    }

    public void setBatch(TraceBatch batch) {
        this.batch = batch;
    }

    public List<TraceLogisticsNode> getLogistics() {
        return logistics;
    }

    public void setLogistics(List<TraceLogisticsNode> logistics) {
        this.logistics = logistics;
    }
}
