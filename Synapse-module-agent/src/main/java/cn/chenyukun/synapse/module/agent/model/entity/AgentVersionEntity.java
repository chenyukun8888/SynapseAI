package cn.chenyukun.synapse.module.agent.model.entity;

import cn.chenyukun.synapse.module.agent.handler.JsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Agent版本实体类
 */
@TableName(value = "agent_versions", autoResultMap = true)
public class AgentVersionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本唯一ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * Agent ID
     */
    private String agentId;

    /**
     * Agent名称
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 描述
     */
    private String description;

    /**
     * 版本号，如1.0.0
     */
    private String versionNumber;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 欢迎消息
     */
    private String welcomeMessage;

    /**
     * 模型配置（JSONB）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Map<String, Object> modelConfig;

    /**
     * 工具配置（JSONB）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<Map<String, Object>> tools;

    /**
     * 知识库ID列表（JSONB）
     */
    @TableField(typeHandler = JsonbTypeHandler.class)
    private List<String> knowledgeBaseIds;

    /**
     * 版本更新日志
     */
    private String changeLog;

    /**
     * Agent类型：1-聊天助手, 2-功能性Agent
     */
    private Integer agentType;

    /**
     * 发布状态：1-审核中, 2-已发布, 3-拒绝, 4-已下架
     */
    private Integer publishStatus;

    /**
     * 审核拒绝原因
     */
    private String rejectReason;

    /**
     * 审核时间
     */
    private LocalDateTime reviewTime;

    /**
     * 发布时间
     */
    private LocalDateTime publishedAt;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 软删除标记
     */
    private LocalDateTime deletedAt;

    public AgentVersionEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }

    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }

    public Map<String, Object> getModelConfig() {
        return modelConfig;
    }

    public void setModelConfig(Map<String, Object> modelConfig) {
        this.modelConfig = modelConfig;
    }

    public List<Map<String, Object>> getTools() {
        return tools;
    }

    public void setTools(List<Map<String, Object>> tools) {
        this.tools = tools;
    }

    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }

    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds;
    }

    public String getChangeLog() {
        return changeLog;
    }

    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
    }

    public Integer getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(Integer publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }

    public LocalDateTime getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(LocalDateTime reviewTime) {
        this.reviewTime = reviewTime;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "AgentVersionEntity{" +
                "id='" + id + '\'' +
                ", agentId='" + agentId + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", description='" + description + '\'' +
                ", versionNumber='" + versionNumber + '\'' +
                ", systemPrompt='" + systemPrompt + '\'' +
                ", welcomeMessage='" + welcomeMessage + '\'' +
                ", modelConfig=" + modelConfig +
                ", tools=" + tools +
                ", knowledgeBaseIds=" + knowledgeBaseIds +
                ", changeLog='" + changeLog + '\'' +
                ", agentType=" + agentType +
                ", publishStatus=" + publishStatus +
                ", rejectReason='" + rejectReason + '\'' +
                ", reviewTime=" + reviewTime +
                ", publishedAt=" + publishedAt +
                ", userId='" + userId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}

