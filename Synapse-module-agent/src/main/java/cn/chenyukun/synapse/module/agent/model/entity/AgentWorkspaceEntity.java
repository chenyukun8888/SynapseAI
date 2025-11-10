package cn.chenyukun.synapse.module.agent.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent工作区实体类
 */
@TableName(value = "agent_workspace")
public class AgentWorkspaceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工作区记录唯一ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * Agent ID
     */
    private String agentId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    public AgentWorkspaceEntity() {
    }

    public AgentWorkspaceEntity(String id, String agentId, String userId, LocalDateTime createdAt) {
        this.id = id;
        this.agentId = agentId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "AgentWorkspaceEntity{" +
                "id='" + id + '\'' +
                ", agentId='" + agentId + '\'' +
                ", userId='" + userId + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

