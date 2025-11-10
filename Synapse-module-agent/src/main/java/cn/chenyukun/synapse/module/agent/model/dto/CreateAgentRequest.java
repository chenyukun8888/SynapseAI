package cn.chenyukun.synapse.module.agent.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 创建Agent请求 DTO
 */
public class CreateAgentRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent名称，2-50字符
     */
    @NotBlank(message = "Agent名称不能为空")
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
     * Agent类型：CHAT/TASK/TOOL，默认CHAT
     */
    private String type;

    /**
     * 系统提示词
     */
    @NotBlank(message = "系统提示词不能为空")
    private String systemPrompt;

    /**
     * 使用的模型，默认deepseek-chat
     */
    private String model;

    /**
     * 温度参数，0.0-1.0，默认0.7
     */
    private Double temperature;

    /**
     * 最大token数，默认2000
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

    /**
     * 是否公开，默认false
     */
    private Boolean isPublic;

    public CreateAgentRequest() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}

