package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.infrastructure.external.llm.SiliconFlowClient;
import cn.chenyukun.synapse.infrastructure.external.llm.SseEmitterManager;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmRequest;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmResponse;
import cn.chenyukun.synapse.module.llm.enums.MessageRole;
import cn.chenyukun.synapse.module.llm.mapper.LlmMessageMapper;
import cn.chenyukun.synapse.module.llm.model.dto.StreamChatRequest;
import cn.chenyukun.synapse.module.llm.model.entity.LlmMessageEntity;
import cn.chenyukun.synapse.module.llm.model.vo.StreamChunkVO;
import cn.chenyukun.synapse.module.llm.service.StreamChatService;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流式对话服务实现
 */
@Service
public class StreamChatServiceImpl implements StreamChatService {

    private final Logger logger = LoggerFactory.getLogger(StreamChatServiceImpl.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Resource
    private SiliconFlowClient siliconFlowClient;

    @Resource
    private SseEmitterManager sseEmitterManager;

    @Resource
    private LlmMessageMapper messageMapper;

    @Override
    @Async
    public void streamChat(StreamChatRequest request, String connectionId) {
        logger.info("开始流式聊天: connectionId={}, message={}", connectionId, request.getMessage());

        String messageId = IdUtil.simpleUUID();
        String model = request.getModel() != null ? request.getModel() : "Qwen/Qwen2.5-7B-Instruct";

        try {
            // 1. 发送 start 事件
            StreamChunkVO startChunk = StreamChunkVO.createStart(
                    messageId,
                    model,
                    dateFormat.format(new Date())
            );
            sseEmitterManager.sendData(connectionId, startChunk.toJson());

            // 2. 构建 LLM 请求
            LlmRequest llmRequest = new LlmRequest();
            llmRequest.addUserMessage(request.getMessage());
            llmRequest.setModel(model);
            llmRequest.setTemperature(request.getTemperature());
            llmRequest.setMaxTokens(request.getMaxTokens());

            // 3. 保存用户消息
            LlmMessageEntity userMessageEntity = new LlmMessageEntity();
            userMessageEntity.setMessageId(IdUtil.simpleUUID());
            userMessageEntity.setRole(MessageRole.USER.getCode());
            userMessageEntity.setContent(request.getMessage());
            userMessageEntity.setModel(model);
            messageMapper.insert(userMessageEntity);

            // 4. 调用流式 LLM
            AtomicInteger chunkIndex = new AtomicInteger(0);
            StringBuilder fullContent = new StringBuilder();

            siliconFlowClient.chatStream(llmRequest, new SiliconFlowClient.StreamCallback() {
                @Override
                public void onChunk(String content, boolean isDone) {
                    if (!isDone && content != null && !content.isEmpty()) {
                        // 发送 chunk 事件
                        StreamChunkVO chunkVO = StreamChunkVO.createChunk(
                                content,
                                chunkIndex.getAndIncrement()
                        );
                        sseEmitterManager.sendData(connectionId, chunkVO.toJson());

                        // 累积完整内容
                        fullContent.append(content);
                    }
                }

                @Override
                public void onError(String error) {
                    logger.error("流式聊天错误: {}", error);
                    // 发送 error 事件
                    StreamChunkVO errorChunk = StreamChunkVO.createError(
                            500,
                            error,
                            dateFormat.format(new Date())
                    );
                    sseEmitterManager.sendData(connectionId, errorChunk.toJson());
                    sseEmitterManager.complete(connectionId);
                }

                @Override
                public void onComplete(LlmResponse.TokenUsage usage) {
                    logger.info("流式聊天完成: messageId={}, tokens={}", messageId, usage.getTotal());

                    // 保存 AI 响应
                    LlmMessageEntity assistantMessageEntity = new LlmMessageEntity();
                    assistantMessageEntity.setMessageId(messageId);
                    assistantMessageEntity.setRole(MessageRole.ASSISTANT.getCode());
                    assistantMessageEntity.setContent(fullContent.toString());
                    assistantMessageEntity.setModel(model);
                    assistantMessageEntity.setTokensUsed(usage.getTotal());
                    messageMapper.insert(assistantMessageEntity);

                    // 发送 done 事件
                    StreamChunkVO.TokenUsage tokenUsage = new StreamChunkVO.TokenUsage(
                            usage.getInput(),
                            usage.getOutput(),
                            usage.getTotal()
                    );
                    StreamChunkVO doneChunk = StreamChunkVO.createDone(
                            tokenUsage,
                            "stop",
                            dateFormat.format(new Date())
                    );
                    sseEmitterManager.sendData(connectionId, doneChunk.toJson());

                    // 关闭连接
                    sseEmitterManager.completeWithMessage(connectionId, "{\"message\":\"Stream completed\"}");
                }
            });

        } catch (Exception e) {
            logger.error("流式聊天异常: connectionId={}", connectionId, e);
            // 发送 error 事件
            StreamChunkVO errorChunk = StreamChunkVO.createError(
                    500,
                    "服务器内部错误: " + e.getMessage(),
                    dateFormat.format(new Date())
            );
            sseEmitterManager.sendData(connectionId, errorChunk.toJson());
            sseEmitterManager.complete(connectionId);
        }
    }
}

