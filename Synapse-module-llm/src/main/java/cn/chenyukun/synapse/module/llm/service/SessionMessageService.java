package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.model.dto.StreamChatRequest;
import cn.chenyukun.synapse.module.llm.model.vo.MessageListVO;

import java.util.List;
import java.util.Map;

/**
 * 会话消息服务接口
 */
public interface SessionMessageService {

    /**
     * 在会话中发送流式消息
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param request 消息请求
     * @param connectionId SSE连接ID
     */
    void sendStreamMessage(String sessionId, String userId, StreamChatRequest request, String connectionId);

    /**
     * 获取会话消息列表
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 消息列表
     */
    MessageListVO getSessionMessages(String sessionId, String userId, Integer page, Integer pageSize);

    /**
     * 获取会话上下文（最近N条消息）
     * @param sessionId 会话ID
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 上下文消息列表
     */
    List<Map<String, Object>> getSessionContext(String sessionId, String userId, Integer limit);
}

