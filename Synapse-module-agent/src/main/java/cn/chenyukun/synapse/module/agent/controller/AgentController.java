package cn.chenyukun.synapse.module.agent.controller;

import cn.chenyukun.synapse.module.agent.model.dto.CreateAgentRequest;
import cn.chenyukun.synapse.module.agent.model.dto.UpdateAgentRequest;
import cn.chenyukun.synapse.module.agent.model.vo.AgentDetailVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentListVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentVO;
import cn.chenyukun.synapse.module.agent.service.AgentService;
import cn.chenyukun.synapse.module.llm.model.vo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Agent管理控制器
 */
@RestController
@RequestMapping("/api/agents")
@Validated
public class AgentController {

    private final Logger logger = LoggerFactory.getLogger(AgentController.class);

    @Resource
    private AgentService agentService;

    private static final String DEFAULT_USER_ID = "default_user";

    /**
     * 创建Agent
     */
    @PostMapping
    public ApiResponse<AgentVO> createAgent(@Valid @RequestBody CreateAgentRequest request) {
        logger.info("收到创建Agent请求: {}", request);
        try {
            AgentVO agent = agentService.createAgent(request, DEFAULT_USER_ID);
            return ApiResponse.success("Agent创建成功", agent);
        } catch (Exception e) {
            logger.error("创建Agent失败: {}", e.getMessage(), e);
            if (e.getMessage().contains("已存在")) {
                return ApiResponse.error(409, e.getMessage());
            }
            return ApiResponse.error("创建Agent失败: " + e.getMessage());
        }
    }

    /**
     * 获取Agent列表
     */
    @GetMapping
    public ApiResponse<AgentListVO> getAgents(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic) {
        logger.info("收到获取Agent列表请求: page={}, pageSize={}, type={}, status={}, keyword={}", 
                page, pageSize, type, status, keyword);
        try {
            AgentListVO result = agentService.getAgentList(page, pageSize, type, status, 
                    keyword, isPublic, DEFAULT_USER_ID);
            return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("获取Agent列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取Agent列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取Agent详情
     */
    @GetMapping("/{agentId}")
    public ApiResponse<AgentDetailVO> getAgentDetail(@PathVariable String agentId) {
        logger.info("收到获取Agent详情请求: agentId={}", agentId);
        try {
            AgentDetailVO agent = agentService.getAgentById(agentId, DEFAULT_USER_ID);
            return ApiResponse.success(agent);
        } catch (Exception e) {
            logger.error("获取Agent详情失败: {}", e.getMessage(), e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.error(404, e.getMessage());
            }
            return ApiResponse.error("获取Agent详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新Agent
     */
    @PutMapping("/{agentId}")
    public ApiResponse<AgentVO> updateAgent(
            @PathVariable String agentId,
            @Valid @RequestBody UpdateAgentRequest request) {
        logger.info("收到更新Agent请求: agentId={}, request={}", agentId, request);
        try {
            AgentVO agent = agentService.updateAgent(agentId, request, DEFAULT_USER_ID);
            return ApiResponse.success("Agent更新成功", agent);
        } catch (Exception e) {
            logger.error("更新Agent失败: {}", e.getMessage(), e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.error(404, e.getMessage());
            }
            if (e.getMessage().contains("已存在")) {
                return ApiResponse.error(409, e.getMessage());
            }
            return ApiResponse.error("更新Agent失败: " + e.getMessage());
        }
    }

    /**
     * 删除Agent
     */
    @DeleteMapping("/{agentId}")
    public ApiResponse<Void> deleteAgent(@PathVariable String agentId) {
        logger.info("收到删除Agent请求: agentId={}", agentId);
        try {
            agentService.deleteAgent(agentId, DEFAULT_USER_ID);
            return ApiResponse.success("Agent已删除", null);
        } catch (Exception e) {
            logger.error("删除Agent失败: {}", e.getMessage(), e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.error(404, e.getMessage());
            }
            return ApiResponse.error("删除Agent失败: " + e.getMessage());
        }
    }

    /**
     * 切换Agent启用/禁用状态
     */
    @PutMapping("/{agentId}/toggle-status")
    public ApiResponse<AgentVO> toggleAgentStatus(@PathVariable String agentId) {
        logger.info("收到切换Agent状态请求: agentId={}", agentId);
        try {
            AgentVO agent = agentService.toggleAgentStatus(agentId, DEFAULT_USER_ID);
            return ApiResponse.success("Agent状态已切换", agent);
        } catch (Exception e) {
            logger.error("切换Agent状态失败: {}", e.getMessage(), e);
            if (e.getMessage().contains("不存在")) {
                return ApiResponse.error(404, e.getMessage());
            }
            return ApiResponse.error("切换Agent状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取已发布的Agent列表
     */
    @GetMapping("/published")
    public ApiResponse<AgentListVO> getPublishedAgents(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            @RequestParam(value = "keyword", required = false) String keyword) {
        logger.info("收到获取已发布Agent列表请求: page={}, pageSize={}, keyword={}", page, pageSize, keyword);
        try {
            // 获取已发布的Agent列表（isPublic=true）
            AgentListVO result = agentService.getAgentList(page, pageSize, null, null, 
                    keyword, true, DEFAULT_USER_ID);
            return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("获取已发布Agent列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取已发布Agent列表失败: " + e.getMessage());
        }
    }
}

