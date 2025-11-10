package cn.chenyukun.synapse.module.agent.model.vo;

import java.io.Serializable;

/**
 * Agent统计信息VO
 */
public class AgentStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer usageCount;
    private Integer sessionCount;
    private Integer avgResponseTime;
    private Double successRate;

    public AgentStatisticsVO() {
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Integer getSessionCount() {
        return sessionCount;
    }

    public void setSessionCount(Integer sessionCount) {
        this.sessionCount = sessionCount;
    }

    public Integer getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(Integer avgResponseTime) {
        this.avgResponseTime = avgResponseTime;
    }

    public Double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(Double successRate) {
        this.successRate = successRate;
    }
}

