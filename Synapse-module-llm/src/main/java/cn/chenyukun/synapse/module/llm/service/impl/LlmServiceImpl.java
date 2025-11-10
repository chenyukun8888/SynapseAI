package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.infrastructure.external.llm.SiliconFlowClient;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmRequest;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmResponse;
import cn.chenyukun.synapse.module.llm.enums.MessageRole;
import cn.chenyukun.synapse.module.llm.model.dto.ChatRequest;
import cn.chenyukun.synapse.module.llm.model.dto.ChatResponse;
import cn.chenyukun.synapse.module.llm.service.LlmService;
import cn.hutool.core.util.IdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * LLM 服务实现（v1.x 兼容接口 - 无会话，不持久化）
 * 注意：此接口仅用于向后兼容，建议使用 v2.0 的会话接口
 */
@Service
public class LlmServiceImpl implements LlmService {
    
    private final Logger logger = LoggerFactory.getLogger(LlmServiceImpl.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    @Resource
    private SiliconFlowClient siliconFlowClient;
    
    @Override
    public ChatResponse chat(ChatRequest request) {
        logger.info("处理聊天请求（无会话模式）: {}", request.getMessage());
        
        // 1. 调用 LLM（无会话，不持久化）
        LlmRequest llmRequest = new LlmRequest();
        llmRequest.addUserMessage(request.getMessage());
        llmRequest.setModel(request.getModel());
        llmRequest.setTemperature(request.getTemperature());
        llmRequest.setMaxTokens(request.getMaxTokens());
        
        LlmResponse llmResponse = siliconFlowClient.chat(llmRequest);
        
        // 2. 构造响应
        ChatResponse response = new ChatResponse();
        response.setId(IdUtil.simpleUUID());
        response.setRole(MessageRole.ASSISTANT.getCode());
        response.setContent(llmResponse.getContent());
        response.setModel(llmResponse.getModel());
        response.setCreatedAt(dateFormat.format(new Date()));
        
        if (llmResponse.getTokenUsage() != null) {
            ChatResponse.TokensUsed tokensUsed = new ChatResponse.TokensUsed(
                llmResponse.getTokenUsage().getInput(),
                llmResponse.getTokenUsage().getOutput(),
                llmResponse.getTokenUsage().getTotal()
            );
            response.setTokensUsed(tokensUsed);
        }
        
        return response;
    }
    
    @Override
    public Map<String, Object> getHistory(Integer limit, Integer offset) {
        logger.info("获取聊天历史（v1.x 无会话模式，无持久化）");
        
        // v2.0: 无会话模式不保存历史，返回空数据
        Map<String, Object> result = new HashMap<>();
        result.put("total", 0);
        result.put("limit", limit != null ? limit : 20);
        result.put("offset", offset != null ? offset : 0);
        result.put("messages", new ArrayList<>());
        result.put("note", "v1.x 兼容接口无会话模式，不保存历史。请使用 v2.0 会话接口。");
        
        return result;
    }
    
    @Override
    public int clearHistory() {
        logger.info("清除聊天历史（v1.x 无会话模式，无历史可清除）");
        return 0;
    }
    
    @Override
    public List<Map<String, Object>> getAvailableModels() {
        List<Map<String, Object>> models = new ArrayList<>();
        
        // SiliconFlow 实际支持的模型（根据官方文档）
        models.add(createModelInfo(
            "Qwen/Qwen2.5-7B-Instruct",
            "通义千问 2.5 7B",
            "SiliconFlow",
            8192,
            true,
            "阿里通义千问 2.5 对话模型"
        ));
        
        models.add(createModelInfo(
            "Qwen/Qwen2.5-Coder-7B-Instruct",
            "通义千问 2.5 代码 7B",
            "SiliconFlow",
            8192,
            true,
            "阿里通义千问 2.5 代码模型"
        ));
        
        models.add(createModelInfo(
            "THUDM/glm-4-9b-chat",
            "智谱 GLM-4 9B",
            "SiliconFlow",
            8192,
            true,
            "智谱 GLM-4 对话模型"
        ));
        
        models.add(createModelInfo(
            "tencent/Hunyuan-MT-7B",
            "腾讯混元 7B",
            "SiliconFlow",
            4096,
            true,
            "腾讯混元多模态模型"
        ));
        
        models.add(createModelInfo(
            "deepseek-ai/DeepSeek-V2.5",
            "DeepSeek V2.5",
            "SiliconFlow",
            8192,
            true,
            "DeepSeek 对话模型"
        ));
        
        return models;
    }
    
    private Map<String, Object> createModelInfo(String name, String displayName, 
                                                  String provider, Integer maxTokens,
                                                  Boolean supportsStreaming, String description) {
        Map<String, Object> model = new HashMap<>();
        model.put("name", name);
        model.put("displayName", displayName);
        model.put("provider", provider);
        model.put("maxTokens", maxTokens);
        model.put("supportsStreaming", supportsStreaming);
        model.put("description", description);
        return model;
    }
}

