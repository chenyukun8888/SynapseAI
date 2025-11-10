package cn.chenyukun.synapse.module.agent.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Agent工作区VO
 */
public class AgentWorkspaceVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String workspaceId;
    private String agentId;
    private String name;
    private Map<String, Object> config;
    private LocalDateTime createdAt;

    public AgentWorkspaceVO() {
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

