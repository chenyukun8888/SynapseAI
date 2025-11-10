package cn.chenyukun.synapse.module.llm.controller;

import cn.chenyukun.synapse.module.llm.model.dto.CreateSessionRequest;
import cn.chenyukun.synapse.module.llm.model.dto.UpdateSessionRequest;
import cn.chenyukun.synapse.module.llm.model.vo.ApiResponse;
import cn.chenyukun.synapse.module.llm.model.vo.SessionListVO;
import cn.chenyukun.synapse.module.llm.model.vo.SessionVO;
import cn.chenyukun.synapse.module.llm.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 会话管理控制器
 */
@RestController
@RequestMapping("/api/sessions")
@Validated
public class SessionController {

    private final Logger logger = LoggerFactory.getLogger(SessionController.class);

    @Resource
    private SessionService sessionService;

    private static final String DEFAULT_USER_ID = "default_user";

    /**
     * 创建会话
     * @param request 创建会话请求
     * @return 会话详情
     */
    @PostMapping
    public ApiResponse<SessionVO> createSession(@Valid @RequestBody CreateSessionRequest request) {
        logger.info("收到创建会话请求: {}", request);
        try {
            SessionVO session = sessionService.createSession(request, DEFAULT_USER_ID);
            return ApiResponse.success(session);
        } catch (Exception e) {
            logger.error("创建会话失败: {}", e.getMessage(), e);
            return ApiResponse.error("创建会话失败: " + e.getMessage());
        }
    }

    /**
     * 获取会话列表
     * @param page 页码
     * @param pageSize 每页数量
     * @param sortBy 排序字段
     * @param order 排序方式
     * @return 会话列表
     */
    @GetMapping
    public ApiResponse<SessionListVO> getSessionList(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "sortBy", required = false, defaultValue = "updatedAt") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "desc") String order) {
        logger.info("收到获取会话列表请求: page={}, pageSize={}, sortBy={}, order={}", page, pageSize, sortBy, order);
        try {
            SessionListVO sessionList = sessionService.getSessionList(DEFAULT_USER_ID, page, pageSize, sortBy, order);
            return ApiResponse.success(sessionList);
        } catch (Exception e) {
            logger.error("获取会话列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取会话列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取会话详情
     * @param sessionId 会话ID
     * @return 会话详情
     */
    @GetMapping("/{sessionId}")
    public ApiResponse<SessionVO> getSessionDetail(@PathVariable String sessionId) {
        logger.info("收到获取会话详情请求: sessionId={}", sessionId);
        try {
            SessionVO session = sessionService.getSessionDetail(sessionId, DEFAULT_USER_ID);
            return ApiResponse.success(session);
        } catch (Exception e) {
            logger.error("获取会话详情失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取会话详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新会话
     * @param sessionId 会话ID
     * @param request 更新会话请求
     * @return 更新后的会话详情
     */
    @PutMapping("/{sessionId}")
    public ApiResponse<SessionVO> updateSession(
            @PathVariable String sessionId,
            @Valid @RequestBody UpdateSessionRequest request) {
        logger.info("收到更新会话请求: sessionId={}, request={}", sessionId, request);
        try {
            SessionVO session = sessionService.updateSession(sessionId, request, DEFAULT_USER_ID);
            return ApiResponse.success("会话更新成功", session);
        } catch (Exception e) {
            logger.error("更新会话失败: {}", e.getMessage(), e);
            return ApiResponse.error("更新会话失败: " + e.getMessage());
        }
    }

    /**
     * 删除会话
     * @param sessionId 会话ID
     * @return 删除结果
     */
    @DeleteMapping("/{sessionId}")
    public ApiResponse<Void> deleteSession(@PathVariable String sessionId) {
        logger.info("收到删除会话请求: sessionId={}", sessionId);
        try {
            sessionService.deleteSession(sessionId, DEFAULT_USER_ID);
            return ApiResponse.success("会话删除成功", null);
        } catch (Exception e) {
            logger.error("删除会话失败: {}", e.getMessage(), e);
            return ApiResponse.error("删除会话失败: " + e.getMessage());
        }
    }

    /**
     * 归档会话
     * @param sessionId 会话ID
     * @return 归档结果
     */
    @PostMapping("/{sessionId}/archive")
    public ApiResponse<Void> archiveSession(@PathVariable String sessionId) {
        logger.info("收到归档会话请求: sessionId={}", sessionId);
        try {
            sessionService.archiveSession(sessionId, DEFAULT_USER_ID);
            return ApiResponse.success("会话归档成功", null);
        } catch (Exception e) {
            logger.error("归档会话失败: {}", e.getMessage(), e);
            return ApiResponse.error("归档会话失败: " + e.getMessage());
        }
    }

    /**
     * 清空会话消息
     * @param sessionId 会话ID
     * @return 清空结果
     */
    @DeleteMapping("/{sessionId}/messages")
    public ApiResponse<Void> clearSessionMessages(@PathVariable String sessionId) {
        logger.info("收到清空会话消息请求: sessionId={}", sessionId);
        try {
            sessionService.clearSessionMessages(sessionId, DEFAULT_USER_ID);
            return ApiResponse.success("会话消息清空成功", null);
        } catch (Exception e) {
            logger.error("清空会话消息失败: {}", e.getMessage(), e);
            return ApiResponse.error("清空会话消息失败: " + e.getMessage());
        }
    }
}

