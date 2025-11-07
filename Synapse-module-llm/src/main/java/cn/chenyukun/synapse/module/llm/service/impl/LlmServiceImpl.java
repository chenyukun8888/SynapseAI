package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.infrastructure.external.llm.SiliconFlowClient;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmRequest;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmResponse;
import cn.chenyukun.synapse.module.llm.converter.LlmMessageConverter;
import cn.chenyukun.synapse.module.llm.enums.MessageRole;
import cn.chenyukun.synapse.module.llm.mapper.LlmMessageMapper;
import cn.chenyukun.synapse.module.llm.model.dto.ChatRequest;
import cn.chenyukun.synapse.module.llm.model.dto.ChatResponse;
import cn.chenyukun.synapse.module.llm.model.entity.LlmMessageEntity;
import cn.chenyukun.synapse.module.llm.model.vo.MessageVO;
import cn.chenyukun.synapse.module.llm.service.LlmService;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * LLM 服务实现
 */
@Service
public class LlmServiceImpl implements LlmService {
    
    private final Logger logger = LoggerFactory.getLogger(LlmServiceImpl.class);
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    @Resource
    private SiliconFlowClient siliconFlowClient;
    
    @Resource
    private LlmMessageMapper messageMapper;
    
    @Override
    public ChatResponse chat(ChatRequest request) {
        logger.info("处理聊天请求: {}", request.getMessage());
        
        // 1. 保存用户消息
        LlmMessageEntity userMessage = new LlmMessageEntity();
        userMessage.setMessageId(IdUtil.simpleUUID());
        userMessage.setRole(MessageRole.USER.getCode());
        userMessage.setContent(request.getMessage());
        userMessage.setCreatedAt(new Date());
        messageMapper.insert(userMessage);
        
        // 2. 调用 LLM
        LlmRequest llmRequest = new LlmRequest();
        llmRequest.addUserMessage(request.getMessage());
        llmRequest.setModel(request.getModel());
        llmRequest.setTemperature(request.getTemperature());
        llmRequest.setMaxTokens(request.getMaxTokens());
        
        LlmResponse llmResponse = siliconFlowClient.chat(llmRequest);
        
        // 3. 保存助手消息
        LlmMessageEntity assistantMessage = new LlmMessageEntity();
        assistantMessage.setMessageId(IdUtil.simpleUUID());
        assistantMessage.setRole(MessageRole.ASSISTANT.getCode());
        assistantMessage.setContent(llmResponse.getContent());
        assistantMessage.setModel(llmResponse.getModel());
        if (llmResponse.getTokenUsage() != null) {
            assistantMessage.setTokensUsed(llmResponse.getTokenUsage().getTotal());
        }
        assistantMessage.setCreatedAt(new Date());
        messageMapper.insert(assistantMessage);
        
        // 4. 构造响应
        ChatResponse response = new ChatResponse();
        response.setId(assistantMessage.getMessageId());
        response.setRole(MessageRole.ASSISTANT.getCode());
        response.setContent(llmResponse.getContent());
        response.setModel(llmResponse.getModel());
        response.setCreatedAt(dateFormat.format(assistantMessage.getCreatedAt()));
        
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
        if (limit == null || limit <= 0) {
            limit = 20;
        }
        if (limit > 100) {
            limit = 100;
        }
        if (offset == null || offset < 0) {
            offset = 0;
        }
        
        // 查询总数
        Long total = messageMapper.selectCount(null);
        
        // 分页查询
        QueryWrapper<LlmMessageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("created_at");
        queryWrapper.last("LIMIT " + limit + " OFFSET " + offset);
        
        List<LlmMessageEntity> entities = messageMapper.selectList(queryWrapper);
        
        // 转换为 VO
        List<MessageVO> messages = new ArrayList<>();
        for (LlmMessageEntity entity : entities) {
            messages.add(LlmMessageConverter.toVO(entity));
        }
        
        // 构造响应
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("limit", limit);
        result.put("offset", offset);
        result.put("messages", messages);
        
        return result;
    }
    
    @Override
    public int clearHistory() {
        logger.info("清除聊天历史");
        return messageMapper.delete(null);
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

