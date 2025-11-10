package cn.chenyukun.synapse.module.agent.service.impl;

import cn.chenyukun.synapse.module.agent.model.vo.AgentVO;
import cn.chenyukun.synapse.module.agent.service.AgentWorkspaceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Agent工作区服务实现类
 */
@Service
public class AgentWorkspaceServiceImpl implements AgentWorkspaceService {

    @Override
    public void addAgentToWorkspace(String agentId, String userId) {
        // TODO: 实现添加Agent到工作区逻辑
    }

    @Override
    public void removeAgentFromWorkspace(String agentId, String userId) {
        // TODO: 实现从工作区移除Agent逻辑
    }

    @Override
    public List<AgentVO> getWorkspaceAgents(String userId) {
        // TODO: 实现查询工作区Agent列表逻辑
        return null;
    }
}

