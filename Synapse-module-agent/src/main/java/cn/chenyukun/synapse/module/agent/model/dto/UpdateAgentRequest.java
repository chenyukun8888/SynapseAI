package cn.chenyukun.synapse.module.agent.model.dto;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 更新Agent请求 DTO
 * 所有字段均为可选，只更新提供的字段
 */
public class UpdateAgentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent名称
     */
    @Size(min = 2, max = 50, message = "Agent名称长度必须在2-50个字符之间")
    private String name;

    /**
     * Agent描述
     */
    private String description;

    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 温度参数，0.0-1.0
     */
    private Double temperature;

    /**
     * 最大token数
     */
    private Integer maxTokens;

    /**
     * 工具列表
     */
    private List<String> tools;

    /**
     * 知识库ID列表
     */
    private List<String> knowledgeBaseIds;

    public UpdateAgentRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
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

    public List<String> getTools() {
        return tools;
    }

    public void setTools(List<String> tools) {
        this.tools = tools;
    }

    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }

    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds;
    }
}

