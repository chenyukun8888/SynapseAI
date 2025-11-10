package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.module.llm.model.dto.ChatRequest;
import cn.chenyukun.synapse.module.llm.model.dto.ChatResponse;
import cn.chenyukun.synapse.module.llm.service.AgentChatService;
import org.springframework.stereotype.Service;

/**
 * Agent聊天服务实现类
 */
@Service
public class AgentChatServiceImpl implements AgentChatService {

    @Override
    public ChatResponse chatWithAgent(String agentId, ChatRequest request, String userId) {
        // TODO: 实现Agent聊天逻辑
        return null;
    }
}

