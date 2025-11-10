package cn.chenyukun.synapse.module.agent.controller;

import cn.chenyukun.synapse.module.agent.model.vo.AgentVO;
import cn.chenyukun.synapse.module.agent.service.AgentWorkspaceService;
import cn.chenyukun.synapse.module.llm.model.vo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Agent工作区管理控制器
 */
@RestController
@RequestMapping("/api/workspace/agents")
@Validated
public class AgentWorkspaceController {

    private final Logger logger = LoggerFactory.getLogger(AgentWorkspaceController.class);

    @Resource
    private AgentWorkspaceService agentWorkspaceService;

    private static final String DEFAULT_USER_ID = "default_user";

    /**
     * 添加Agent到工作区
     */
    @PostMapping("/{agentId}")
    public ApiResponse<Void> addAgentToWorkspace(@PathVariable String agentId) {
        // TODO: 实现添加Agent到工作区接口
        return null;
    }

    /**
     * 从工作区移除Agent
     */
    @DeleteMapping("/{agentId}")
    public ApiResponse<Void> removeAgentFromWorkspace(@PathVariable String agentId) {
        // TODO: 实现从工作区移除Agent接口
        return null;
    }

    /**
     * 获取工作区Agent列表
     */
    @GetMapping
    public ApiResponse<List<AgentVO>> getWorkspaceAgents() {
        // TODO: 实现获取工作区Agent列表接口
        return null;
    }
}

