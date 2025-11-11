package cn.chenyukun.synapse.module.llm.model.dto;

import cn.chenyukun.synapse.module.llm.enums.OverflowStrategy;

import java.util.List;

/**
 * 上下文窗口DTO
 * 表示会话的上下文窗口信息，包含消息列表和Token管理配置
 */
public class ContextWindowDTO {

    /** 会话ID */
    private String sessionId;

    /** 消息列表 */
    private List<TokenUsageDTO> messages;

    /** 当前Token总数 */
    private Integer currentTokens;

    /** 最大Token限制 */
    private Integer maxTokens;

    /** 溢出策略 */
    private OverflowStrategy overflowStrategy;

    /** 摘要阈值 */
    private Integer summaryThreshold;

    /** 预留比例 */
    private Double reserveRatio;

    /** 是否已处理 */
    private Boolean processed;

    /** 摘要内容 */
    private String summary;

    // Default constructor
    public ContextWindowDTO() {
    }

    // Constructor with sessionId
    public ContextWindowDTO(String sessionId) {
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public List<TokenUsageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<TokenUsageDTO> messages) {
        this.messages = messages;
    }

    public Integer getCurrentTokens() {
        return currentTokens;
    }

    public void setCurrentTokens(Integer currentTokens) {
        this.currentTokens = currentTokens;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public OverflowStrategy getOverflowStrategy() {
        return overflowStrategy;
    }

    public void setOverflowStrategy(OverflowStrategy overflowStrategy) {
        this.overflowStrategy = overflowStrategy;
    }

    public Integer getSummaryThreshold() {
        return summaryThreshold;
    }

    public void setSummaryThreshold(Integer summaryThreshold) {
        this.summaryThreshold = summaryThreshold;
    }

    public Double getReserveRatio() {
        return reserveRatio;
    }

    public void setReserveRatio(Double reserveRatio) {
        this.reserveRatio = reserveRatio;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
