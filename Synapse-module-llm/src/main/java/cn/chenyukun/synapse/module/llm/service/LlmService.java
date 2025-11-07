package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.model.dto.ChatRequest;
import cn.chenyukun.synapse.module.llm.model.dto.ChatResponse;
import cn.chenyukun.synapse.module.llm.model.vo.MessageVO;

import java.util.List;
import java.util.Map;

/**
 * LLM 服务接口
 */
public interface LlmService {
    
    /**
     * 发送聊天消息
     */
    ChatResponse chat(ChatRequest request);
    
    /**
     * 获取聊天历史
     */
    Map<String, Object> getHistory(Integer limit, Integer offset);
    
    /**
     * 清除聊天历史
     */
    int clearHistory();
    
    /**
     * 获取可用模型列表
     */
    List<Map<String, Object>> getAvailableModels();
}

