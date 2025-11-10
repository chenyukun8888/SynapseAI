package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.model.dto.ChatRequest;
import cn.chenyukun.synapse.module.llm.model.dto.ChatResponse;

/**
 * Agent聊天服务接口
 */
public interface AgentChatService {

    /**
     * 使用Agent进行聊天
     */
    ChatResponse chatWithAgent(String agentId, ChatRequest request, String userId);
}

