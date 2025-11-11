package cn.chenyukun.synapse.module.llm.model.dto;

import java.time.LocalDateTime;

/**
 * Token使用情况DTO
 * 用于传递Token相关信息
 */
public class TokenUsageDTO {

    /** 消息ID */
    private String id;

    /** 消息内容 */
    private String content;

    /** 消息角色 */
    private String role;

    /** Token数量 */
    private Integer tokenCount;

    /** 消息本体Token数量（不含系统提示等） */
    private Integer bodyTokenCount;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 会话ID */
    private String sessionId;

    /** 用户ID */
    private String userId;

    /** 模型名称 */
    private String modelName;

    // Default constructor
    public TokenUsageDTO() {
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with content and role
    public TokenUsageDTO(String content, String role) {
        this.content = content;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    // Constructor with full parameters
    public TokenUsageDTO(String id, String content, String role, Integer tokenCount, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.role = role;
        this.tokenCount = tokenCount;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }

    public Integer getBodyTokenCount() {
        return bodyTokenCount;
    }

    public void setBodyTokenCount(Integer bodyTokenCount) {
        this.bodyTokenCount = bodyTokenCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}
