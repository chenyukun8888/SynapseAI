package cn.chenyukun.synapse.module.agent.service;

import cn.chenyukun.synapse.module.agent.model.dto.CreateVersionRequest;
import cn.chenyukun.synapse.module.agent.model.vo.AgentVersionItemVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentVersionListVO;

/**
 * Agent版本服务接口
 */
public interface AgentVersionService {

    /**
     * 创建Agent版本
     */
    AgentVersionItemVO createVersion(String agentId, CreateVersionRequest request, String userId);

    /**
     * 获取Agent版本列表
     */
    AgentVersionListVO getVersionsByAgentId(String agentId, String userId);

    /**
     * 发布Agent版本
     */
    AgentVersionItemVO publishVersion(String agentId, String versionId, String userId);
}

