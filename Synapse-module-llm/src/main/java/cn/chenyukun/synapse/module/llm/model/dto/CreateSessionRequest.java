package cn.chenyukun.synapse.module.llm.model.dto;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 创建会话请求 DTO
 */
public class CreateSessionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话标题
     */
    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 系统提示词
     */
    private String systemPrompt;

    /**
     * 会话描述
     */
    private String description;

    public CreateSessionRequest() {
    }

    public CreateSessionRequest(String title, String model, String systemPrompt, String description) {
        this.title = title;
        this.model = model;
        this.systemPrompt = systemPrompt;
        this.description = description;
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

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CreateSessionRequest{" +
                "title='" + title + '\'' +
                ", model='" + model + '\'' +
                ", systemPrompt='" + systemPrompt + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

