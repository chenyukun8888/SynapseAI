package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.infrastructure.external.llm.SiliconFlowClient;
import cn.chenyukun.synapse.infrastructure.external.llm.SseEmitterManager;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmRequest;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmResponse;
import cn.chenyukun.synapse.module.llm.mapper.MessageMapper;
import cn.chenyukun.synapse.module.llm.mapper.SessionMapper;
import cn.chenyukun.synapse.module.llm.model.dto.StreamChatRequest;
import cn.chenyukun.synapse.module.llm.model.entity.MessageEntity;
import cn.chenyukun.synapse.module.llm.model.entity.SessionEntity;
import cn.chenyukun.synapse.module.llm.model.vo.MessageListVO;
import cn.chenyukun.synapse.module.llm.model.vo.StreamChunkVO;
import cn.chenyukun.synapse.module.llm.service.SessionMessageService;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 会话消息服务实现
 */
@Service
public class SessionMessageServiceImpl implements SessionMessageService {

    private final Logger logger = LoggerFactory.getLogger(SessionMessageServiceImpl.class);

    @Resource
    private SessionMapper sessionMapper;

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private SiliconFlowClient siliconFlowClient;

    @Resource
    private SseEmitterManager sseEmitterManager;

    private static final String DEFAULT_MODEL = "Qwen/Qwen2.5-7B-Instruct";
    private static final int CONTEXT_LIMIT = 20; // 上下文消息数量限制

    @Override
    @Async
    @Transactional(rollbackFor = Exception.class)
    public void sendStreamMessage(String sessionId, String userId, StreamChatRequest request, String connectionId) {
        logger.info("在会话中发送流式消息: sessionId={}, userId={}, connectionId={}", sessionId, userId, connectionId);

        // 验证会话权限
        SessionEntity session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            logger.error("会话不存在或无权访问: sessionId={}, userId={}", sessionId, userId);
            sseEmitterManager.sendData(connectionId, 
                    StreamChunkVO.error(403, "会话不存在或无权访问"));
            sseEmitterManager.complete(connectionId);
            return;
        }

        String messageId = IdUtil.simpleUUID();
        String model = request.getModel() != null ? request.getModel() : 
                       (session.getModel() != null ? session.getModel() : DEFAULT_MODEL);

        try {
            // 发送开始事件（包含 sessionId，前端需要用它来维护上下文）
            sseEmitterManager.sendData(connectionId, StreamChunkVO.start(messageId, model, sessionId));

            // 保存用户消息
            MessageEntity userMessage = new MessageEntity();
            userMessage.setId(IdUtil.simpleUUID());
            userMessage.setSessionId(sessionId);
            userMessage.setRole("user");
            userMessage.setContent(request.getMessage());
            userMessage.setModel(model);
            userMessage.setProvider("user");
            userMessage.setCreatedAt(LocalDateTime.now());
            messageMapper.insert(userMessage);

            // 获取会话上下文
            List<MessageEntity> contextMessages = getContextMessages(sessionId, CONTEXT_LIMIT - 2);
            
            // 构建 LLM 请求
            LlmRequest llmRequest = new LlmRequest();
            
            // 添加系统提示词（如果有）
            if (session.getSystemPrompt() != null && !session.getSystemPrompt().isEmpty()) {
                llmRequest.addSystemMessage(session.getSystemPrompt());
            }
            
            // 添加上下文消息（已包含刚保存的用户消息，不需要再次添加）
            for (MessageEntity msg : contextMessages) {
                if ("user".equals(msg.getRole())) {
                    llmRequest.addUserMessage(msg.getContent());
                } else if ("assistant".equals(msg.getRole())) {
                    llmRequest.addAssistantMessage(msg.getContent());
                }
            }
            
            llmRequest.setModel(model);
            llmRequest.setTemperature(request.getTemperature());
            llmRequest.setMaxTokens(request.getMaxTokens());
            llmRequest.setStream(true);

            StringBuilder fullContent = new StringBuilder();
            AtomicInteger chunkIndex = new AtomicInteger(0);

            // 调用 LLM 流式接口
            siliconFlowClient.chatStream(llmRequest, new SiliconFlowClient.StreamCallback() {
                @Override
                public void onChunk(String content, boolean isDone) {
                    if (content != null && !content.isEmpty()) {
                        fullContent.append(content);
                        sseEmitterManager.sendData(connectionId,
                                StreamChunkVO.chunk(content, chunkIndex.getAndIncrement()));
                    }
                }

                @Override
                public void onError(String error) {
                    logger.error("LLM 流式服务错误: sessionId={}, error={}", sessionId, error);
                    sseEmitterManager.sendData(connectionId, StreamChunkVO.error(500, error));
                    sseEmitterManager.complete(connectionId);
                    
                    // 保存错误消息
                    saveAssistantMessage(sessionId, messageId, "错误: " + error, model, null);
                    updateSessionTimestamp(sessionId);
                }

                @Override
                public void onComplete(LlmResponse.TokenUsage usage) {
                    logger.info("LLM 流式服务完成: sessionId={}, tokens={}", sessionId, usage);
                    
                    // 发送完成事件
                    sseEmitterManager.sendData(connectionId,
                            StreamChunkVO.done(usage, "stop"));
                    sseEmitterManager.complete(connectionId);
                    
                    // 保存 AI 响应
                    saveAssistantMessage(sessionId, messageId, fullContent.toString(), model, usage.getTotal());
                    updateSessionTimestamp(sessionId);
                }
            });

        } catch (Exception e) {
            logger.error("处理流式消息时发生异常: sessionId={}, error={}", sessionId, e.getMessage(), e);
            sseEmitterManager.sendData(connectionId, 
                    StreamChunkVO.error(500, "服务器内部错误: " + e.getMessage()));
            sseEmitterManager.complete(connectionId);
            
            // 保存错误消息
            saveAssistantMessage(sessionId, messageId, "错误: " + e.getMessage(), model, null);
            updateSessionTimestamp(sessionId);
        }
    }

    @Override
    public MessageListVO getSessionMessages(String sessionId, String userId, Integer page, Integer pageSize) {
        logger.info("获取会话消息列表: sessionId={}, userId={}, page={}, pageSize={}", 
                sessionId, userId, page, pageSize);

        // 验证会话权限
        SessionEntity session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在或无权访问");
        }

        // 参数校验
        page = (page == null || page < 1) ? 1 : page;
        pageSize = (pageSize == null || pageSize < 1) ? 50 : Math.min(pageSize, 100);

        // 查询消息总数
        long total = messageMapper.countBySessionId(sessionId);

        // 查询消息列表
        List<MessageEntity> entities = messageMapper.selectBySessionIdOrderByCreatedAt(sessionId);

        // 分页处理
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, entities.size());
        List<MessageEntity> pagedEntities = fromIndex < entities.size() 
                ? entities.subList(fromIndex, toIndex) 
                : new ArrayList<>();

        // 转换为 VO
        List<MessageListVO.MessageItemVO> messageVOs = new ArrayList<>();
        for (MessageEntity entity : pagedEntities) {
            MessageListVO.MessageItemVO itemVO = new MessageListVO.MessageItemVO();
            itemVO.setId(entity.getId());
            itemVO.setRole(entity.getRole());
            itemVO.setContent(entity.getContent());
            itemVO.setModel(entity.getModel());
            itemVO.setTokenCount(entity.getTokenCount());
            itemVO.setCreatedAt(entity.getCreatedAt());
            messageVOs.add(itemVO);
        }

        MessageListVO result = new MessageListVO();
        result.setSessionId(sessionId);
        result.setTotal(total);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotalPages((int) Math.ceil((double) total / pageSize));
        result.setMessages(messageVOs);

        return result;
    }

    @Override
    public List<Map<String, Object>> getSessionContext(String sessionId, String userId, Integer limit) {
        logger.info("获取会话上下文: sessionId={}, userId={}, limit={}", sessionId, userId, limit);

        // 验证会话权限
        SessionEntity session = sessionMapper.selectById(sessionId);
        if (session == null || !session.getUserId().equals(userId)) {
            throw new RuntimeException("会话不存在或无权访问");
        }

        limit = (limit == null || limit < 1) ? 20 : Math.min(limit, 50);

        List<MessageEntity> messages = getContextMessages(sessionId, limit);
        List<Map<String, Object>> context = new ArrayList<>();

        for (MessageEntity message : messages) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("role", message.getRole());
            messageMap.put("content", message.getContent());
            context.add(messageMap);
        }

        return context;
    }

    /**
     * 获取上下文消息
     */
    private List<MessageEntity> getContextMessages(String sessionId, int limit) {
        List<MessageEntity> allMessages = messageMapper.selectBySessionIdOrderByCreatedAt(sessionId);
        
        // 获取最近的 N 条消息
        int size = allMessages.size();
        if (size <= limit) {
            return allMessages;
        }
        
        return allMessages.subList(size - limit, size);
    }

    /**
     * 保存 AI 响应消息
     */
    private void saveAssistantMessage(String sessionId, String messageId, String content, 
                                     String model, Integer tokenCount) {
        MessageEntity assistantMessage = new MessageEntity();
        assistantMessage.setId(messageId);
        assistantMessage.setSessionId(sessionId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(content);
        assistantMessage.setModel(model);
        assistantMessage.setProvider("SiliconFlow");
        assistantMessage.setTokenCount(tokenCount);
        assistantMessage.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(assistantMessage);
    }

    /**
     * 更新会话时间戳
     */
    private void updateSessionTimestamp(String sessionId) {
        SessionEntity session = sessionMapper.selectById(sessionId);
        if (session != null) {
            session.setUpdatedAt(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }
}

