package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.model.dto.TokenOverflowResult;
import cn.chenyukun.synapse.module.llm.model.dto.TokenUsageDTO;
import cn.chenyukun.synapse.module.llm.service.TokenOverflowStrategy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 截断策略实现类
 * 不对消息进行任何处理，直接返回所有消息
 */
@Service
public class TruncationStrategy implements TokenOverflowStrategy {

    /** 策略配置 */
    private final TokenOverflowConfig config;

    /** 构造函数 */
    public TruncationStrategy() {
        this.config = new TokenOverflowConfig();
    }

    /** 带配置的构造函数 */
    public TruncationStrategy(TokenOverflowConfig config) {
        this.config = config != null ? config : new TokenOverflowConfig();
    }

    @Override
    public TokenOverflowResult process(List<TokenUsageDTO> messages, TokenOverflowConfig configParam) {
        TokenOverflowResult result = new TokenOverflowResult();
        result.setRetainedMessages(messages);
        result.setStrategyName(getStrategyName());
        result.setProcessed(false);
        result.setTotalTokens(calculateTotalTokens(messages));
        return result;
    }

    @Override
    public String getStrategyName() {
        return "TRUNCATION";
    }

    @Override
    public boolean needsProcessing(List<TokenUsageDTO> messages, TokenOverflowConfig configParam) {
        // 截断策略始终不需要处理
        return false;
    }

    private int calculateTotalTokens(List<TokenUsageDTO> messages) {
        return messages.stream()
                .mapToInt(dto -> dto.getBodyTokenCount() != null ? dto.getBodyTokenCount() : 0)
                .sum();
    }
}
