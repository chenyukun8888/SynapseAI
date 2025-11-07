package cn.chenyukun.synapse.module.llm.model.dto;

import javax.validation.constraints.NotBlank;

/**
 * 聊天请求 DTO
 */
public class ChatRequest {
    
    @NotBlank(message = "消息内容不能为空")
    private String message;
    
    private String model;
    private Double temperature;
    private Integer maxTokens;
    
    public ChatRequest() {
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
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
}

