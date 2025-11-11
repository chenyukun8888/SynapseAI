package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.model.dto.TokenOverflowResult;
import cn.chenyukun.synapse.module.llm.model.dto.TokenUsageDTO;

import java.util.List;

/**
 * Token管理器接口
 * 负责Token计算、溢出处理和管理
 */
public interface TokenManager {

    /**
     * 计算消息列表的总Token数
     *
     * @param messages 消息列表
     * @return 总Token数
     */
    int calculateTotalTokens(List<TokenUsageDTO> messages);

    /**
     * 处理Token溢出
     *
     * @param messages 消息列表
     * @param config Token配置
     * @return 处理结果
     */
    TokenOverflowResult processTokenOverflow(List<TokenUsageDTO> messages,
                                           TokenOverflowConfig config);

    /**
     * 记录Token使用情况
     *
     * @param sessionId 会话ID
     * @param tokenUsage Token使用情况
     */
    void recordTokenUsage(String sessionId, TokenUsageDTO tokenUsage);

    /**
     * 获取会话的Token使用统计
     *
     * @param sessionId 会话ID
     * @return Token使用统计
     */
    TokenUsageDTO getTokenUsageStats(String sessionId);

    /**
     * 检查Token是否超限
     *
     * @param currentTokens 当前Token数
     * @param maxTokens 最大Token限制
     * @return 是否超限
     */
    boolean isTokenOverflow(int currentTokens, int maxTokens);
}
