package cn.chenyukun.synapse.module.llm.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话详情 VO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String title;
    private String model;
    private String systemPrompt;
    private String description;
    private Integer messageCount;
    private Long tokensUsed;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updatedAt;
    
    private Boolean isArchived;

    public SessionVO() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public Long getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(Long tokensUsed) {
        this.tokensUsed = tokensUsed;
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

    @Override
    public String toString() {
        return "SessionVO{" +
                "sessionId='" + sessionId + '\'' +
                ", title='" + title + '\'' +
                ", model='" + model + '\'' +
                ", messageCount=" + messageCount +
                ", tokensUsed=" + tokensUsed +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

