package cn.chenyukun.synapse.module.llm.controller;

import cn.chenyukun.synapse.infrastructure.external.llm.SseEmitterManager;
import cn.chenyukun.synapse.module.llm.model.dto.StreamChatRequest;
import cn.chenyukun.synapse.module.llm.model.vo.ApiResponse;
import cn.chenyukun.synapse.module.llm.service.SessionMessageService;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 会话消息控制器
 */
@RestController
@RequestMapping("/api/sessions")
@Validated
public class SessionMessageController {

    private final Logger logger = LoggerFactory.getLogger(SessionMessageController.class);

    @Resource
    private SessionMessageService sessionMessageService;

    @Resource
    private SseEmitterManager sseEmitterManager;

    private static final String DEFAULT_USER_ID = "default_user";

    /**
     * 测试接口 - 验证连接
     */
    @GetMapping("/test")
    public ApiResponse<String> test() {
        logger.info("收到测试请求");
        return ApiResponse.success("连接成功！后端服务正常运行");
    }

    /**
     * 在会话中发送流式消息
     * @param sessionId 会话ID
     * @param request 消息请求
     * @return SseEmitter
     */
    @PostMapping(value = "/{sessionId}/messages/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sendStreamMessage(
            @PathVariable String sessionId,
            @Valid @RequestBody StreamChatRequest request) {
        
        String connectionId = IdUtil.simpleUUID();
        logger.info("========== 收到流式聊天请求 ==========");
        logger.info("sessionId: {}", sessionId);
        logger.info("connectionId: {}", connectionId);
        logger.info("message: {}", request != null ? request.getMessage() : "null");
        logger.info("request: {}", request);
        logger.info("=====================================");

        // 创建 SSE 连接
        SseEmitter emitter = sseEmitterManager.createEmitter(connectionId);

        // 异步处理流式聊天
        sessionMessageService.sendStreamMessage(sessionId, DEFAULT_USER_ID, request, connectionId);

        return emitter;
    }

    /**
     * 获取会话的消息列表
     * @param sessionId 会话ID
     * @param page 页码
     * @param pageSize 每页数量
     * @return 消息列表
     */
    @GetMapping("/{sessionId}/messages")
    public ApiResponse<?> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize) {
        
        logger.info("获取会话消息列表: sessionId={}, page={}, pageSize={}", sessionId, page, pageSize);

        try {
            return ApiResponse.success(
                    sessionMessageService.getSessionMessages(sessionId, DEFAULT_USER_ID, page, pageSize));
        } catch (Exception e) {
            logger.error("获取会话消息失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取会话消息失败: " + e.getMessage());
        }
    }

    /**
     * 获取会话上下文
     * @param sessionId 会话ID
     * @param limit 限制数量
     * @return 上下文消息列表
     */
    @GetMapping("/{sessionId}/context")
    public ApiResponse<?> getSessionContext(
            @PathVariable String sessionId,
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        
        logger.info("获取会话上下文: sessionId={}, limit={}", sessionId, limit);

        try {
            return ApiResponse.success(
                    sessionMessageService.getSessionContext(sessionId, DEFAULT_USER_ID, limit));
        } catch (Exception e) {
            logger.error("获取会话上下文失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取会话上下文失败: " + e.getMessage());
        }
    }
}

