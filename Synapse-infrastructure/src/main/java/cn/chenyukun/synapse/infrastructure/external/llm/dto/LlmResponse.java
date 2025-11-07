package cn.chenyukun.synapse.infrastructure.external.llm.dto;

/**
 * LLM 响应
 */
public class LlmResponse {
    
    private String content;
    private String provider;
    private String model;
    private String finishReason;
    private TokenUsage tokenUsage;
    
    public LlmResponse() {
    }
    
    public LlmResponse(String content) {
        this.content = content;
    }
    
    // Getters and Setters
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getFinishReason() {
        return finishReason;
    }
    
    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }
    
    public TokenUsage getTokenUsage() {
        return tokenUsage;
    }
    
    public void setTokenUsage(TokenUsage tokenUsage) {
        this.tokenUsage = tokenUsage;
    }
    
    /**
     * Token 使用情况
     */
    public static class TokenUsage {
        private Integer input;
        private Integer output;
        private Integer total;
        
        public TokenUsage() {
        }
        
        public TokenUsage(Integer input, Integer output, Integer total) {
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

