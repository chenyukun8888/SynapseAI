package cn.chenyukun.synapse.module.llm.model.dto;

/**
 * 流式聊天请求
 */
public class StreamChatRequest {

    /**
     * 用户消息内容
     */
    private String message;

    /**
     * 模型名称
     */
    private String model;

    /**
     * 温度参数，0.0-1.0
     */
    private Double temperature;

    /**
     * 最大生成 token 数
     */
    private Integer maxTokens;

    public StreamChatRequest() {
    }

    public StreamChatRequest(String message, String model, Double temperature, Integer maxTokens) {
        this.message = message;
        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
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

    @Override
    public String toString() {
        return "StreamChatRequest{" +
                "message='" + message + '\'' +
                ", model='" + model + '\'' +
                ", temperature=" + temperature +
                ", maxTokens=" + maxTokens +
                '}';
    }
}

