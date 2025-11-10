package cn.chenyukun.synapse.module.llm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 会话实体类
 */
@TableName(value = "sessions", autoResultMap = true)
public class SessionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话唯一ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 关联的Agent ID
     */
    private String agentId;

    /**
     * 会话描述
     */
    private String description;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 是否归档
     */
    private Boolean isArchived;

    /**
     * 元数据（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    public SessionEntity() {
    }

    public SessionEntity(String id, String title, String userId, String agentId, String description, 
                        String model, String systemPrompt, LocalDateTime createdAt, 
                        LocalDateTime updatedAt, Boolean isArchived, Map<String, Object> metadata) {
        this.id = id;
        this.title = title;
        this.userId = userId;
        this.agentId = agentId;
        this.description = description;
        this.model = model;
        this.systemPrompt = systemPrompt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isArchived = isArchived;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
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

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "SessionEntity{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", userId='" + userId + '\'' +
                ", agentId='" + agentId + '\'' +
                ", description='" + description + '\'' +
                ", model='" + model + '\'' +
                ", systemPrompt='" + systemPrompt + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isArchived=" + isArchived +
                ", metadata=" + metadata +
                '}';
    }
}

