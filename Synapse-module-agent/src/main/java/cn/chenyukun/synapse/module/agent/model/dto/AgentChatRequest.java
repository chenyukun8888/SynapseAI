package cn.chenyukun.synapse.module.agent.model.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Agent聊天请求 DTO
 */
public class AgentChatRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 会话ID，不提供则自动创建新会话
     */
    private String sessionId;

    /**
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 是否流式输出，默认false
     */
    private Boolean stream;

    public AgentChatRequest() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getStream() {
        return stream;
    }

    public void setStream(Boolean stream) {
        this.stream = stream;
    }
}

