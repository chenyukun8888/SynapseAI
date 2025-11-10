package cn.chenyukun.synapse.module.llm.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息实体类
 */
@TableName(value = "messages", autoResultMap = true)
public class MessageEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息唯一ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 所属会话ID
     */
    private String sessionId;

    /**
     * 角色：user/assistant/system
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * Token数量
     */
    private Integer tokenCount;

    /**
     * LLM提供商
     */
    private String provider;

    /**
     * 使用的模型
     */
    private String model;

    /**
     * 元数据（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> metadata;

    public MessageEntity() {
    }

    public MessageEntity(String id, String sessionId, String role, String content, 
                        LocalDateTime createdAt, Integer tokenCount, String provider, 
                        String model, Map<String, Object> metadata) {
        this.id = id;
        this.sessionId = sessionId;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
        this.tokenCount = tokenCount;
        this.provider = provider;
        this.model = model;
        this.metadata = metadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
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

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "MessageEntity{" +
                "id='" + id + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", role='" + role + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", tokenCount=" + tokenCount +
                ", provider='" + provider + '\'' +
                ", model='" + model + '\'' +
                ", metadata=" + metadata +
                '}';
    }
}

