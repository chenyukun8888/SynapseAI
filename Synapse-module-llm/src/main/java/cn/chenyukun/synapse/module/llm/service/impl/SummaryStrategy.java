package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.model.dto.TokenOverflowResult;
import cn.chenyukun.synapse.module.llm.model.dto.TokenUsageDTO;
import cn.chenyukun.synapse.module.llm.service.TokenOverflowStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 摘要策略实现类
 * 将超出阈值的早期消息生成摘要，保留摘要和最新消息
 */
@Service
public class SummaryStrategy implements TokenOverflowStrategy {

    /** 策略配置 */
    private final TokenOverflowConfig config;

    /** 需要进行摘要的消息 */
    private List<TokenUsageDTO> messagesToSummarize;

    /** 生成的摘要消息对象 */
    private TokenUsageDTO summaryMessage;

    public SummaryStrategy() {
        this.config = new TokenOverflowConfig();
        this.messagesToSummarize = new ArrayList<>();
        this.summaryMessage = null;
    }

    public SummaryStrategy(TokenOverflowConfig config) {
        this.config = config;
        this.messagesToSummarize = new ArrayList<>();
        this.summaryMessage = null;
    }

    @Override
    public TokenOverflowResult process(List<TokenUsageDTO> messages, TokenOverflowConfig tokenOverflowConfig) {
        if (!needsProcessing(messages, tokenOverflowConfig)) {
            TokenOverflowResult result = new TokenOverflowResult();
            result.setRetainedMessages(messages);
            result.setStrategyName(getStrategyName());
            result.setProcessed(false);
            result.setTotalTokens(calculateTotalTokens(messages));
            return result;
        }

        // 按时间排序
        List<TokenUsageDTO> sortedMessages = messages.stream()
                .sorted(Comparator.comparing(TokenUsageDTO::getCreatedAt))
                .collect(Collectors.toList());

        // 获取需要保留的消息数量
        int threshold = tokenOverflowConfig.getSummaryThreshold() != null ?
                tokenOverflowConfig.getSummaryThreshold() : 20;

        // 分割消息
        messagesToSummarize = sortedMessages.subList(0, sortedMessages.size() - threshold);
        List<TokenUsageDTO> retainedMessages = new ArrayList<>(
                sortedMessages.subList(sortedMessages.size() - threshold, sortedMessages.size()));

        // 生成新的摘要消息
        TokenUsageDTO newSummary = generateSummary(messagesToSummarize, messages);
        // 添加摘要消息到活跃消息列表
        retainedMessages.add(0, newSummary);

        // 最终验证：确保结果在阈值范围内
        TokenOverflowResult result = validateAndAdjustResult(retainedMessages, tokenOverflowConfig, messages);
        result.setSummary(newSummary.getContent());
        result.setStrategyName(getStrategyName());
        result.setProcessed(true);

        return result;
    }

    @Override
    public String getStrategyName() {
        return "SUMMARY";
    }

    @Override
    public boolean needsProcessing(List<TokenUsageDTO> messages, TokenOverflowConfig configParam) {
        if (messages == null || messages.isEmpty()) {
            return false;
        }

        // 优先使用方法参数，否则使用实例配置
        TokenOverflowConfig effectiveConfig = configParam != null ? configParam : this.config;

        // 检查消息数量阈值
        int threshold = effectiveConfig.getSummaryThreshold() != null ? effectiveConfig.getSummaryThreshold() : 20;
        boolean exceedsMessageThreshold = messages.size() > threshold;

        // 检查Token总数阈值
        int totalTokens = calculateTotalTokens(messages);
        int maxTokens = effectiveConfig.getMaxTokens() != null ? effectiveConfig.getMaxTokens() : 4096;
        boolean exceedsTokenThreshold = totalTokens > maxTokens;

        // 只要有一个条件满足就触发摘要
        return exceedsMessageThreshold || exceedsTokenThreshold;
    }

    /**
     * 生成摘要内容并更新摘要消息记录
     */
    private TokenUsageDTO generateSummary(List<TokenUsageDTO> messages, List<TokenUsageDTO> historyMessages) {
        // 简化的摘要生成逻辑（可扩展为LLM调用）
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("对话摘要：");

        for (TokenUsageDTO message : messages) {
            String role = message.getRole();
            if ("user".equalsIgnoreCase(role)) {
                summaryBuilder.append("用户询问；");
            } else if ("assistant".equalsIgnoreCase(role)) {
                summaryBuilder.append("AI回答；");
            }
        }

        String summaryContent = summaryBuilder.toString();
        if (summaryContent.endsWith("；")) {
            summaryContent = summaryContent.substring(0, summaryContent.length() - 1);
        }

        // 创建摘要消息
        TokenUsageDTO summaryMessage = new TokenUsageDTO();
        summaryMessage.setId("summary-" + System.currentTimeMillis());
        summaryMessage.setContent(summaryContent);
        summaryMessage.setRole("SUMMARY");
        summaryMessage.setTokenCount(50); // 估算摘要token数
        summaryMessage.setBodyTokenCount(50);
        summaryMessage.setCreatedAt(getEarliestTime(historyMessages));

        return summaryMessage;
    }

    /**
     * 获取最早的时间点，用于设置摘要消息的时间
     */
    private LocalDateTime getEarliestTime(List<TokenUsageDTO> messages) {
        return messages.stream()
                .filter(message -> !"SUMMARY".equals(message.getRole()))
                .map(TokenUsageDTO::getCreatedAt)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now().minusSeconds(1));
    }

    /**
     * 验证并调整结果，确保最终Token数在阈值范围内
     *
     * @param retainedMessages 当前保留的消息（包括摘要）
     * @param config 配置
     * @param allMessages 所有原始消息
     * @return 调整后的结果
     */
    private TokenOverflowResult validateAndAdjustResult(List<TokenUsageDTO> retainedMessages,
                                                       TokenOverflowConfig config,
                                                       List<TokenUsageDTO> allMessages) {
        int currentTokens = calculateTotalTokens(retainedMessages);
        int maxTokens = config.getMaxTokens() != null ? config.getMaxTokens() : 4096;

        // 如果已经在阈值范围内，直接返回
        if (currentTokens <= maxTokens) {
            TokenOverflowResult result = new TokenOverflowResult();
            result.setRetainedMessages(retainedMessages);
            result.setTotalTokens(currentTokens);
            result.setOriginalMessageCount(allMessages.size());
            return result;
        }

        // 如果仍然超出阈值，使用滑动窗口策略进一步削减
        return forceReduceToThreshold(retainedMessages, maxTokens, allMessages);
    }

    /**
     * 强制削减到阈值范围内（摘要策略的兜底处理）
     * 当摘要后仍然超出阈值时，进一步移除旧消息
     *
     * @param messages 当前消息列表（包含摘要）
     * @param maxTokens 最大Token限制
     * @param allMessages 所有原始消息
     * @return 强制调整后的结果
     */
    private TokenOverflowResult forceReduceToThreshold(List<TokenUsageDTO> messages,
                                                      int maxTokens,
                                                      List<TokenUsageDTO> allMessages) {
        // 分离摘要消息和其他消息
        TokenUsageDTO summaryMessage = null;
        List<TokenUsageDTO> otherMessages = new ArrayList<>();

        for (TokenUsageDTO message : messages) {
            if ("SUMMARY".equals(message.getRole())) {
                summaryMessage = message;
            } else {
                otherMessages.add(message);
            }
        }

        // 从其他消息中选择最新的，直到达到Token上限
        otherMessages.sort(Comparator.comparing(TokenUsageDTO::getCreatedAt).reversed());

        List<TokenUsageDTO> finalMessages = new ArrayList<>();
        int totalTokens = 0;

        // 先尝试添加摘要（如果有空间）
        if (summaryMessage != null) {
            int summaryTokens = summaryMessage.getBodyTokenCount() != null ?
                    summaryMessage.getBodyTokenCount() : 50;
            if (summaryTokens <= maxTokens) {
                finalMessages.add(summaryMessage);
                totalTokens += summaryTokens;
            }
        }

        // 添加其他消息
        for (TokenUsageDTO message : otherMessages) {
            int messageTokens = message.getBodyTokenCount() != null ? message.getBodyTokenCount() : 0;

            // 如果是第一条消息且超过剩余空间，仍然保留
            if (finalMessages.isEmpty() && messageTokens > (maxTokens - totalTokens)) {
                finalMessages.add(message);
                totalTokens = messageTokens;
                break;
            }

            // 检查是否有足够空间
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

    /** 获取需要摘要的消息列表 */
    public List<TokenUsageDTO> getMessagesToSummarize() {
        return messagesToSummarize;
    }

    /** 获取生成的摘要消息对象 */
    public TokenUsageDTO getSummaryMessage() {
        return summaryMessage;
    }
}
