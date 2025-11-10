package cn.chenyukun.synapse.module.llm.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息列表 VO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
    private List<MessageItemVO> messages;

    public MessageListVO() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<MessageItemVO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageItemVO> messages) {
        this.messages = messages;
    }

    /**
     * 消息项 VO
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class MessageItemVO implements Serializable {
        private static final long serialVersionUID = 1L;

        private String id;
        private String role;
        private String content;
        private String model;
        private Integer tokenCount;
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
        private LocalDateTime createdAt;

        public MessageItemVO() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
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

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public Integer getTokenCount() {
            return tokenCount;
        }

        public void setTokenCount(Integer tokenCount) {
            this.tokenCount = tokenCount;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        @Override
        public String toString() {
            return "MessageItemVO{" +
                    "id='" + id + '\'' +
                    ", role='" + role + '\'' +
                    ", content='" + content + '\'' +
                    ", model='" + model + '\'' +
                    ", tokenCount=" + tokenCount +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MessageListVO{" +
                "sessionId='" + sessionId + '\'' +
                ", total=" + total +
                ", page=" + page +
                ", pageSize=" + pageSize +
                ", totalPages=" + totalPages +
                ", messages=" + messages +
                '}';
    }
}

