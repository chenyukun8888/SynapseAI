package cn.chenyukun.synapse.module.llm.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 会话列表项 VO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionListItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String title;
    private String model;
    private Integer messageCount;
    private LastMessageVO lastMessage;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private LocalDateTime updatedAt;

    public SessionListItemVO() {
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

    public Integer getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount) {
        this.messageCount = messageCount;
    }

    public LastMessageVO getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(LastMessageVO lastMessage) {
        this.lastMessage = lastMessage;
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

    @Override
    public String toString() {
        return "SessionListItemVO{" +
                "sessionId='" + sessionId + '\'' +
                ", title='" + title + '\'' +
                ", model='" + model + '\'' +
                ", messageCount=" + messageCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    /**
     * 最后一条消息 VO
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LastMessageVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String role;
        private String content;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private LocalDateTime createdAt;

        public LastMessageVO() {
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "LastMessageVO{" +
                    "role='" + role + '\'' +
                    ", content='" + content + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
}

