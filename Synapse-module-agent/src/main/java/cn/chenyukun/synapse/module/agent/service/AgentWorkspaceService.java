package cn.chenyukun.synapse.module.agent.service;

import cn.chenyukun.synapse.module.agent.model.vo.AgentVO;

import java.util.List;

/**
 * Agent工作区服务接口
 */
public interface AgentWorkspaceService {

    /**
     * 添加Agent到工作区
     */
    void addAgentToWorkspace(String agentId, String userId);

    /**
     * 从工作区移除Agent
     */
    void removeAgentFromWorkspace(String agentId, String userId);

    /**
     * 查询用户工作区Agent列表
     */
    List<AgentVO> getWorkspaceAgents(String userId);
}

