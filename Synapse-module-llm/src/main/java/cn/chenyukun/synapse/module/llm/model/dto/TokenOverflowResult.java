package cn.chenyukun.synapse.module.llm.model.dto;

import java.util.List;

/**
 * Token溢出处理结果
 */
public class TokenOverflowResult {

    /** 处理后保留的消息列表 */
    private List<TokenUsageDTO> retainedMessages;

    /** 被移除消息的摘要（如果有的话） */
    private String summary;

    /** 处理后的总token数 */
    private int totalTokens;

    /** 使用的策略名称 */
    private String strategyName;

    /** 是否进行了处理 true: 消息被处理过（如被截断、摘要等） false: 消息未经处理（原样返回） */
    private boolean processed;

    /** 处理耗时（毫秒） */
    private long processingTime;

    /** 处理的原始消息数量 */
    private int originalMessageCount;

    /** 移除的消息数量 */
    private int removedMessageCount;

    /** 默认构造函数 */
    public TokenOverflowResult() {
    }

    /** 带参数的构造函数 */
    public TokenOverflowResult(List<TokenUsageDTO> retainedMessages, String strategyName, boolean processed) {
        this.retainedMessages = retainedMessages;
        this.strategyName = strategyName;
        this.processed = processed;
        this.originalMessageCount = retainedMessages != null ? retainedMessages.size() : 0;
    }

    // Getters and Setters
    public List<TokenUsageDTO> getRetainedMessages() {
        return retainedMessages;
    }

    public void setRetainedMessages(List<TokenUsageDTO> retainedMessages) {
        this.retainedMessages = retainedMessages;
        if (retainedMessages != null) {
            this.totalTokens = retainedMessages.stream()
                    .mapToInt(dto -> dto.getBodyTokenCount() != null ? dto.getBodyTokenCount() : 0)
                    .sum();
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(int totalTokens) {
        this.totalTokens = totalTokens;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public int getOriginalMessageCount() {
        return originalMessageCount;
    }

    public void setOriginalMessageCount(int originalMessageCount) {
        this.originalMessageCount = originalMessageCount;
        this.removedMessageCount = this.originalMessageCount -
                (retainedMessages != null ? retainedMessages.size() : 0);
    }

    public int getRemovedMessageCount() {
        return removedMessageCount;
    }

    public void setRemovedMessageCount(int removedMessageCount) {
        this.removedMessageCount = removedMessageCount;
    }

    /** 获取保留消息的数量 */
    public int getRetainedMessageCount() {
        return retainedMessages != null ? retainedMessages.size() : 0;
    }

    /** 计算Token节省量 */
    public int getTokenSaved() {
        // 估算被移除消息的平均token数
        if (removedMessageCount == 0) {
            return 0;
        }
        int avgTokenPerMessage = totalTokens / getRetainedMessageCount();
        return removedMessageCount * avgTokenPerMessage;
    }

    @Override
    public String toString() {
        return "TokenOverflowResult{" +
                "strategyName='" + strategyName + '\'' +
                ", processed=" + processed +
                ", originalMessages=" + originalMessageCount +
                ", retainedMessages=" + getRetainedMessageCount() +
                ", removedMessages=" + removedMessageCount +
                ", totalTokens=" + totalTokens +
                ", processingTime=" + processingTime + "ms" +
                ", hasSummary=" + (summary != null && !summary.isEmpty()) +
                '}';
    }
}
