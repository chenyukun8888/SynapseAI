package cn.chenyukun.synapse.module.agent.controller;

import cn.chenyukun.synapse.module.agent.model.vo.AgentVO;
import cn.chenyukun.synapse.module.llm.model.vo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员Agent管理控制器
 */
@RestController
@RequestMapping("/api/admin/agents")
@Validated
public class AdminAgentController {

    private final Logger logger = LoggerFactory.getLogger(AdminAgentController.class);

    /**
     * 审核Agent
     */
    @PostMapping("/{id}/review")
    public ApiResponse<Void> reviewAgent(
            @PathVariable String id,
            @RequestParam Integer status,
            @RequestParam(required = false) String rejectReason) {
        // TODO: 实现审核Agent接口
        return null;
    }

    /**
     * 获取待审核Agent列表
     */
    @GetMapping("/pending")
    public ApiResponse<List<AgentVO>> getPendingAgents() {
        // TODO: 实现获取待审核Agent列表接口
        return null;
    }
}

