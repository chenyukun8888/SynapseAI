package cn.chenyukun.synapse.module.agent.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Agent详情视图对象
 */
public class AgentDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String agentId;
    private String name;
    private String description;
    private String avatar;
    private String type;
    private String systemPrompt;
    private String model;
    private Double temperature;
    private Integer maxTokens;
    private List<ToolVO> tools;
    private List<String> knowledgeBaseIds;
    private String status;
    private Boolean isPublic;
    private String version;
    private AgentStatisticsVO statistics;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 工具VO
     */
    public static class ToolVO implements Serializable {
        private String name;
        private String displayName;
        private Boolean enabled;

        public ToolVO() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public AgentDetailVO() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public List<ToolVO> getTools() {
        return tools;
    }

    public void setTools(List<ToolVO> tools) {
        this.tools = tools;
    }

    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }

    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public AgentStatisticsVO getStatistics() {
        return statistics;
    }

    public void setStatistics(AgentStatisticsVO statistics) {
        this.statistics = statistics;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

