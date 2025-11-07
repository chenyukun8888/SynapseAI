package cn.chenyukun.synapse.module.llm.controller;

import cn.chenyukun.synapse.infrastructure.external.llm.SseEmitterManager;
import cn.chenyukun.synapse.module.llm.model.dto.StreamChatRequest;
import cn.chenyukun.synapse.module.llm.service.StreamChatService;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;

/**
 * 流式对话控制器
 */
@RestController
@RequestMapping("/api/chat")
public class StreamChatController {

    private final Logger logger = LoggerFactory.getLogger(StreamChatController.class);

    @Resource
    private SseEmitterManager sseEmitterManager;

    @Resource
    private StreamChatService streamChatService;

    /**
     * 流式聊天接口
     *
     * @param request 聊天请求
     * @return SseEmitter
     */
    @PostMapping("/stream")
    public SseEmitter streamChat(@RequestBody StreamChatRequest request) {
        // 生成唯一的连接ID
        String connectionId = IdUtil.simpleUUID();

        logger.info("创建流式聊天连接: connectionId={}, message={}", 
                connectionId, request.getMessage());

        // 创建 SSE 连接
        SseEmitter emitter = sseEmitterManager.createEmitter(connectionId);

        // 异步处理流式聊天
        streamChatService.streamChat(request, connectionId);

        return emitter;
    }

    /**
     * 获取当前活跃连接数（用于监控）
     *
     * @return 连接数
     */
    @GetMapping("/stream/connections")
    public int getActiveConnections() {
        return sseEmitterManager.getActiveConnectionCount();
    }
}

