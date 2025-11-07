package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.model.dto.StreamChatRequest;

/**
 * 流式对话服务接口
 */
public interface StreamChatService {

    /**
     * 发送流式聊天消息
     * 
     * @param request 流式聊天请求
     * @param connectionId SSE 连接ID
     */
    void streamChat(StreamChatRequest request, String connectionId);
}

