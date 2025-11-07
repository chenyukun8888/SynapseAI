package cn.chenyukun.synapse.module.llm.model.dto;

/**
 * 聊天响应 DTO
 */
public class ChatResponse {
    
    private String id;
    private String role;
    private String content;
    private String model;
    private TokensUsed tokensUsed;
    private String createdAt;
    
    public ChatResponse() {
    }
    
    // Getters and Setters
    
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
    
    public TokensUsed getTokensUsed() {
        return tokensUsed;
    }
    
    public void setTokensUsed(TokensUsed tokensUsed) {
        this.tokensUsed = tokensUsed;
    }
    
    public String getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Token 使用情况
     */
    public static class TokensUsed {
        private Integer input;
        private Integer output;
        private Integer total;
        
        public TokensUsed() {
        }
        
        public TokensUsed(Integer input, Integer output, Integer total) {
            this.input = input;
            this.output = output;
            this.total = total;
        }
        
        public Integer getInput() {
            return input;
        }
        
        public void setInput(Integer input) {
            this.input = input;
        }
        
        public Integer getOutput() {
            return output;
        }
        
        public void setOutput(Integer output) {
            this.output = output;
        }
        
        public Integer getTotal() {
            return total;
        }
        
        public void setTotal(Integer total) {
            this.total = total;
        }
    }
}

