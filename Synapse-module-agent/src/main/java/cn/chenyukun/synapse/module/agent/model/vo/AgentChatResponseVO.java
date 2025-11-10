package cn.chenyukun.synapse.module.agent.model.vo;

import cn.chenyukun.synapse.module.llm.model.dto.ChatResponse;
import java.io.Serializable;

/**
 * Agent聊天响应VO
 */
public class AgentChatResponseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sessionId;
    private String agentId;
    private String agentName;
    private String messageId;
    private MessageVO userMessage;
    private ChatResponse assistantMessage;

    /**
     * 消息VO
     */
    public static class MessageVO implements Serializable {
        private String id;
        private String role;
        private String content;
        private String createdAt;

        public MessageVO() {
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

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }
    }

    public AgentChatResponseVO() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public MessageVO getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(MessageVO userMessage) {
        this.userMessage = userMessage;
    }

    public ChatResponse getAssistantMessage() {
        return assistantMessage;
    }

    public void setAssistantMessage(ChatResponse assistantMessage) {
        this.assistantMessage = assistantMessage;
    }
}

