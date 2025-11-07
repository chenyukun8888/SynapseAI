package cn.chenyukun.synapse.infrastructure.external.llm.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * LLM 请求
 */
public class LlmRequest {
    
    private String model;
    private List<LlmMessage> messages;
    private Double temperature;
    private Integer maxTokens;
    private Boolean stream;
    
    public LlmRequest() {
        this.messages = new ArrayList<>();
    }
    
    public void addMessage(String role, String content) {
        messages.add(new LlmMessage(role, content));
    }
    
    public void addUserMessage(String content) {
        addMessage("user", content);
    }
    
    public void addSystemMessage(String content) {
        addMessage("system", content);
    }
    
    public void addAssistantMessage(String content) {
        addMessage("assistant", content);
    }
    
    // Getters and Setters
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public List<LlmMessage> getMessages() {
        return messages;
    }
    
    public void setMessages(List<LlmMessage> messages) {
        this.messages = messages;
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
    
    public Boolean getStream() {
        return stream;
    }
    
    public void setStream(Boolean stream) {
        this.stream = stream;
    }
    
    /**
     * LLM 消息
     */
    public static class LlmMessage {
        private String role;
        private String content;
        
        public LlmMessage() {
        }
        
        public LlmMessage(String role, String content) {
            this.role = role;
            this.content = content;
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
    }
}

