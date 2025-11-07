package cn.chenyukun.synapse.module.llm.controller;

import cn.chenyukun.synapse.module.llm.common.Result;
import cn.chenyukun.synapse.module.llm.model.dto.ChatRequest;
import cn.chenyukun.synapse.module.llm.model.dto.ChatResponse;
import cn.chenyukun.synapse.module.llm.service.LlmService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天控制器
 */
@RestController
@RequestMapping("/api")
public class ChatController {
    
    private final Logger logger = LoggerFactory.getLogger(ChatController.class);
    
    @Resource
    private LlmService llmService;
    
    /**
     * 发送聊天消息
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody @Validated ChatRequest request) {
        logger.info("收到聊天请求: message={}, model={}", request.getMessage(), request.getModel());
        
        try {
            ChatResponse response = llmService.chat(request);
            return Result.success(response);
        } catch (Exception e) {
            logger.error("处理聊天请求异常", e);
            return Result.serverError("LLM 服务异常: " + e.getMessage());
        }
    }
    
    /**
     * 获取聊天历史
     */
    @GetMapping("/chat/history")
    public Result<Map<String, Object>> getHistory(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        logger.info("获取聊天历史: limit={}, offset={}", limit, offset);
        
        try {
            Map<String, Object> history = llmService.getHistory(limit, offset);
            return Result.success(history);
        } catch (Exception e) {
            logger.error("获取聊天历史异常", e);
            return Result.serverError("获取聊天历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 清除聊天历史
     */
    @DeleteMapping("/chat/history")
    public Result<Map<String, Object>> clearHistory() {
        logger.info("清除聊天历史");
        
        try {
            int deletedCount = llmService.clearHistory();
            Map<String, Object> data = new HashMap<>();
            data.put("deletedCount", deletedCount);
            return Result.success("聊天历史已清除", data);
        } catch (Exception e) {
            logger.error("清除聊天历史异常", e);
            return Result.serverError("清除聊天历史失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取可用模型列表
     */
    @GetMapping("/models")
    public Result<Map<String, Object>> getModels() {
        logger.info("获取可用模型列表");
        
        try {
            List<Map<String, Object>> models = llmService.getAvailableModels();
            Map<String, Object> data = new HashMap<>();
            data.put("models", models);
            return Result.success(data);
        } catch (Exception e) {
            logger.error("获取模型列表异常", e);
            return Result.serverError("获取模型列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("SynapseAI 服务正常运行中");
    }
}

