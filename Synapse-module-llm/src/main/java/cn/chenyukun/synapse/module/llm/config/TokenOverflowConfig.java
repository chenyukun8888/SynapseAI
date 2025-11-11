package cn.chenyukun.synapse.module.llm.config;

/**
 * Token溢出处理配置类
 * 包含所有Token策略相关的配置参数
 */
public class TokenOverflowConfig {

    /** 策略类型 */
    private String strategyType;

    /** 最大Token数，适用于滑动窗口和摘要策略 */
    private Integer maxTokens;

    /** 预留缓冲比例，适用于滑动窗口策略 范围0-1之间的小数，表示预留的空间比例 */
    private Double reserveRatio;

    /** 摘要触发阈值（消息数量），适用于摘要策略 */
    private Integer summaryThreshold;

    /** LLM提供商配置，用于摘要策略生成摘要 */
    private Object providerConfig;

    /** 会话ID */
    private String sessionId;

    /** 用户ID */
    private String userId;

    /** 默认构造函数 */
    public TokenOverflowConfig() {
        this.strategyType = "TRUNCATION";
        this.maxTokens = 4096;
        this.reserveRatio = 0.1;
        this.summaryThreshold = 20;
    }

    /** 带策略类型的构造函数 */
    public TokenOverflowConfig(String strategyType) {
        this();
        this.strategyType = strategyType;
    }

    /** 完整参数构造函数 */
    public TokenOverflowConfig(String strategyType, Integer maxTokens, Double reserveRatio, Integer summaryThreshold) {
        this.strategyType = strategyType;
        this.maxTokens = maxTokens;
        this.reserveRatio = reserveRatio;
        this.summaryThreshold = summaryThreshold;
    }

    // Getters and Setters
    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Double getReserveRatio() {
        return reserveRatio;
    }

    public void setReserveRatio(Double reserveRatio) {
        this.reserveRatio = reserveRatio;
    }

    public Integer getSummaryThreshold() {
        return summaryThreshold;
    }

    public void setSummaryThreshold(Integer summaryThreshold) {
        this.summaryThreshold = summaryThreshold;
    }

    public Object getProviderConfig() {
        return providerConfig;
    }

    public void setProviderConfig(Object providerConfig) {
        this.providerConfig = providerConfig;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** 创建默认的无策略配置 */
    public static TokenOverflowConfig createDefault() {
        return new TokenOverflowConfig("TRUNCATION");
    }

    /** 创建滑动窗口策略配置 */
    public static TokenOverflowConfig createSlidingWindowConfig(int maxTokens, Double reserveRatio) {
        TokenOverflowConfig config = new TokenOverflowConfig("SLIDING_WINDOW");
        config.setMaxTokens(maxTokens);
        config.setReserveRatio(reserveRatio != null ? reserveRatio : 0.1);
        return config;
    }

    /** 创建摘要策略配置 */
    public static TokenOverflowConfig createSummaryConfig(int maxTokens, Integer summaryThreshold) {
        TokenOverflowConfig config = new TokenOverflowConfig("SUMMARY");
        config.setMaxTokens(maxTokens);
        config.setSummaryThreshold(summaryThreshold != null ? summaryThreshold : 20);
        return config;
    }

    /** 根据模型类型创建推荐配置 */
    public static TokenOverflowConfig createRecommendedConfig(String modelType) {
        switch (modelType.toLowerCase()) {
            case "gpt-4":
            case "gpt-4-turbo":
                return createSlidingWindowConfig(8192, 0.1);
            case "gpt-3.5-turbo":
                return createSlidingWindowConfig(4096, 0.1);
            case "claude-3":
                return createSummaryConfig(200000, 50);
            default:
                return createSlidingWindowConfig(4096, 0.1);
        }
    }

    @Override
    public String toString() {
        return "TokenOverflowConfig{" +
                "strategyType='" + strategyType + '\'' +
                ", maxTokens=" + maxTokens +
                ", reserveRatio=" + reserveRatio +
                ", summaryThreshold=" + summaryThreshold +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
