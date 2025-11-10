package cn.chenyukun.synapse.module.agent.service;

import cn.chenyukun.synapse.module.agent.model.dto.CreateAgentRequest;
import cn.chenyukun.synapse.module.agent.model.dto.UpdateAgentRequest;
import cn.chenyukun.synapse.module.agent.model.vo.AgentDetailVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentListVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentVO;

/**
 * Agent服务接口
 */
public interface AgentService {

    /**
     * 创建Agent
     */
    AgentVO createAgent(CreateAgentRequest request, String userId);

    /**
     * 获取Agent列表（支持分页、筛选、搜索）
     */
    AgentListVO getAgentList(Integer page, Integer pageSize, String type, String status, 
                             String keyword, Boolean isPublic, String userId);

    /**
     * 获取Agent详情
     */
    AgentDetailVO getAgentById(String agentId, String userId);

    /**
     * 更新Agent
     */
    AgentVO updateAgent(String agentId, UpdateAgentRequest request, String userId);

    /**
     * 删除Agent
     */
    void deleteAgent(String agentId, String userId);

    /**
     * 切换Agent启用/禁用状态
     */
    AgentVO toggleAgentStatus(String agentId, String userId);
}

