package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.model.dto.TokenOverflowResult;
import cn.chenyukun.synapse.module.llm.model.dto.TokenUsageDTO;
import cn.chenyukun.synapse.module.llm.service.TokenOverflowStrategy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 滑动窗口策略实现类
 * 基于Token数量保留最新消息，超出窗口的旧消息将被丢弃
 */
@Service
public class SlidingWindowStrategy implements TokenOverflowStrategy {

    private static final int DEFAULT_MAX_TOKENS = 4096;
    private static final double DEFAULT_RESERVE_RATIO = 0.1;

    /** 策略配置 */
    private final TokenOverflowConfig config;

    /** 构造函数 */
    public SlidingWindowStrategy() {
        this.config = new TokenOverflowConfig();
    }

    /** 带配置的构造函数 */
    public SlidingWindowStrategy(TokenOverflowConfig config) {
        this.config = config != null ? config : new TokenOverflowConfig();
    }

    @Override
    public TokenOverflowResult process(List<TokenUsageDTO> messages, TokenOverflowConfig configParam) {
        // 优先使用方法参数，否则使用实例配置
        TokenOverflowConfig effectiveConfig = configParam != null ? configParam : this.config;

        if (!needsProcessing(messages, effectiveConfig)) {
            TokenOverflowResult result = new TokenOverflowResult();
            result.setRetainedMessages(messages);
            result.setStrategyName(getStrategyName());
            result.setProcessed(false);
            result.setTotalTokens(calculateTotalTokens(messages));
            return result;
        }

        // 按时间排序，保留最新的消息
        List<TokenUsageDTO> sortedMessages = new ArrayList<>(messages);
        sortedMessages.sort(Comparator.comparing(TokenUsageDTO::getCreatedAt).reversed());

        // 计算可用token数（考虑预留空间）
        int maxTokens = effectiveConfig.getMaxTokens() != null ? effectiveConfig.getMaxTokens() : DEFAULT_MAX_TOKENS;
        double reserveRatio = effectiveConfig.getReserveRatio() != null ? effectiveConfig.getReserveRatio() : DEFAULT_RESERVE_RATIO;
        int reserveTokens = (int) (maxTokens * reserveRatio);
        int availableTokens = maxTokens - reserveTokens;

        // 保留最新的消息，直到达到token限制
        List<TokenUsageDTO> retainedMessages = new ArrayList<>();
        int totalTokens = 0;

        for (TokenUsageDTO message : sortedMessages) {
            int messageTokens = message.getBodyTokenCount() != null ? message.getBodyTokenCount() : 0;
            if (totalTokens + messageTokens <= availableTokens) {
                retainedMessages.add(message);
                totalTokens += messageTokens;
            } else {
                break;
            }
        }

        // 最终验证：确保结果在阈值范围内
        TokenOverflowResult result = validateAndAdjustResult(retainedMessages, effectiveConfig, sortedMessages);
        result.setStrategyName(getStrategyName());
        result.setProcessed(true);

        return result;
    }

    @Override
    public String getStrategyName() {
        return "SLIDING_WINDOW";
    }

    @Override
    public boolean needsProcessing(List<TokenUsageDTO> messages, TokenOverflowConfig configParam) {
        if (messages == null || messages.isEmpty()) {
            return false;
        }

        // 优先使用方法参数，否则使用实例配置
        TokenOverflowConfig effectiveConfig = configParam != null ? configParam : this.config;

        int totalTokens = calculateTotalTokens(messages);
        int maxTokens = effectiveConfig.getMaxTokens() != null ? effectiveConfig.getMaxTokens() : DEFAULT_MAX_TOKENS;
        return totalTokens > maxTokens;
    }

    /**
     * 验证并调整结果，确保最终Token数在阈值范围内
     *
     * @param retainedMessages 当前保留的消息
     * @param config 配置
     * @param allMessages 所有原始消息（用于进一步处理）
     * @return 调整后的结果
     */
    private TokenOverflowResult validateAndAdjustResult(List<TokenUsageDTO> retainedMessages,
                                                       TokenOverflowConfig config,
                                                       List<TokenUsageDTO> allMessages) {
        int currentTokens = calculateTotalTokens(retainedMessages);
        int maxTokens = config.getMaxTokens() != null ? config.getMaxTokens() : DEFAULT_MAX_TOKENS;

        // 如果已经在阈值范围内，直接返回
        if (currentTokens <= maxTokens) {
            TokenOverflowResult result = new TokenOverflowResult();
            result.setRetainedMessages(retainedMessages);
            result.setTotalTokens(currentTokens);
            result.setOriginalMessageCount(allMessages.size());
            return result;
        }

        // 如果仍然超出阈值，需要进一步削减
        return forceReduceToThreshold(retainedMessages, maxTokens, allMessages);
    }

    /**
     * 强制削减到阈值范围内（极端情况下的兜底处理）
     *
     * @param messages 当前消息列表
     * @param maxTokens 最大Token限制
     * @param allMessages 所有原始消息
     * @return 强制调整后的结果
     */
    private TokenOverflowResult forceReduceToThreshold(List<TokenUsageDTO> messages,
                                                      int maxTokens,
                                                      List<TokenUsageDTO> allMessages) {
        // 从最新的消息开始，强制保留直到达到Token上限
        List<TokenUsageDTO> sortedMessages = new ArrayList<>(messages);
        sortedMessages.sort(Comparator.comparing(TokenUsageDTO::getCreatedAt).reversed());

        List<TokenUsageDTO> finalMessages = new ArrayList<>();
        int totalTokens = 0;

        for (TokenUsageDTO message : sortedMessages) {
            int messageTokens = message.getBodyTokenCount() != null ? message.getBodyTokenCount() : 0;

            // 如果单条消息就超过阈值，只保留这条最新消息
            if (finalMessages.isEmpty() && messageTokens > maxTokens) {
                finalMessages.add(message);
                totalTokens = messageTokens;
                break;
            }

            // 尝试添加消息
            if (totalTokens + messageTokens <= maxTokens) {
                finalMessages.add(message);
                totalTokens += messageTokens;
            } else {
                break;
            }
        }

        TokenOverflowResult result = new TokenOverflowResult();
        result.setRetainedMessages(finalMessages);
        result.setTotalTokens(totalTokens);
        result.setOriginalMessageCount(allMessages.size());
        result.setRemovedMessageCount(allMessages.size() - finalMessages.size());

        return result;
    }

    private int calculateTotalTokens(List<TokenUsageDTO> messages) {
        return messages.stream()
                .mapToInt(dto -> dto.getBodyTokenCount() != null ? dto.getBodyTokenCount() : 0)
                .sum();
    }
}
