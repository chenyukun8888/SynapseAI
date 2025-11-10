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
 * Agent实体类
 */
@TableName(value = "agents", autoResultMap = true)
public class AgentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent唯一ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

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
     * 当前发布的版本ID
     */
    private String publishedVersion;

    /**
     * Agent状态：false-禁用，true-启用
     */
    private Boolean enabled;

    /**
     * Agent类型：1-聊天助手, 2-功能性Agent
     */
    private Integer agentType;

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

    public AgentEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getPublishedVersion() {
        return publishedVersion;
    }

    public void setPublishedVersion(String publishedVersion) {
        this.publishedVersion = publishedVersion;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getAgentType() {
        return agentType;
    }

    public void setAgentType(Integer agentType) {
        this.agentType = agentType;
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
        return "AgentEntity{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", description='" + description + '\'' +
                ", systemPrompt='" + systemPrompt + '\'' +
                ", welcomeMessage='" + welcomeMessage + '\'' +
                ", modelConfig=" + modelConfig +
                ", tools=" + tools +
                ", knowledgeBaseIds=" + knowledgeBaseIds +
                ", publishedVersion='" + publishedVersion + '\'' +
                ", enabled=" + enabled +
                ", agentType=" + agentType +
                ", userId='" + userId + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", deletedAt=" + deletedAt +
                '}';
    }
}

