package cn.chenyukun.synapse.module.agent.controller;

import cn.chenyukun.synapse.module.agent.model.dto.CreateVersionRequest;
import cn.chenyukun.synapse.module.agent.model.vo.AgentVersionItemVO;
import cn.chenyukun.synapse.module.agent.model.vo.AgentVersionListVO;
import cn.chenyukun.synapse.module.agent.service.AgentVersionService;
import cn.chenyukun.synapse.module.llm.model.vo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * Agent版本管理控制器
 */
@RestController
@RequestMapping("/api/agents/{agentId}/versions")
@Validated
public class AgentVersionController {

    private final Logger logger = LoggerFactory.getLogger(AgentVersionController.class);

    @Resource
    private AgentVersionService agentVersionService;

    private static final String DEFAULT_USER_ID = "default_user";

    /**
     * 创建Agent版本
     */
    @PostMapping
    public ApiResponse<AgentVersionItemVO> createVersion(
            @PathVariable String agentId,
            @Valid @RequestBody CreateVersionRequest request) {
        logger.info("收到创建版本请求: agentId={}, request={}", agentId, request);
        try {
            AgentVersionItemVO version = agentVersionService.createVersion(agentId, request, DEFAULT_USER_ID);
            return ApiResponse.success("版本创建成功", version);
        } catch (Exception e) {
            logger.error("创建版本失败: {}", e.getMessage(), e);
            return ApiResponse.error("创建版本失败: " + e.getMessage());
        }
    }

    /**
     * 获取Agent版本列表
     */
    @GetMapping
    public ApiResponse<AgentVersionListVO> getVersions(@PathVariable String agentId) {
        logger.info("收到获取版本列表请求: agentId={}", agentId);
        try {
            AgentVersionListVO result = agentVersionService.getVersionsByAgentId(agentId, DEFAULT_USER_ID);
            return ApiResponse.success(result);
        } catch (Exception e) {
            logger.error("获取版本列表失败: {}", e.getMessage(), e);
            return ApiResponse.error("获取版本列表失败: " + e.getMessage());
        }
    }

    /**
     * 发布Agent版本
     */
    @PostMapping("/{versionId}/publish")
    public ApiResponse<AgentVersionItemVO> publishVersion(
            @PathVariable String agentId,
            @PathVariable String versionId) {
        logger.info("收到发布版本请求: agentId={}, versionId={}", agentId, versionId);
        try {
            AgentVersionItemVO version = agentVersionService.publishVersion(agentId, versionId, DEFAULT_USER_ID);
            return ApiResponse.success("版本发布成功", version);
        } catch (Exception e) {
            logger.error("发布版本失败: {}", e.getMessage(), e);
            return ApiResponse.error("发布版本失败: " + e.getMessage());
        }
    }
}

