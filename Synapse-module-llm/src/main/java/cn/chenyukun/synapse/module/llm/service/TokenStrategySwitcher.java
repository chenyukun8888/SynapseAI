package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.enums.OverflowStrategy;
import cn.chenyukun.synapse.module.llm.model.dto.TokenOverflowResult;
import cn.chenyukun.synapse.module.llm.model.dto.TokenUsageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Token策略切换器
 * 提供动态切换和执行不同Token处理策略的功能
 */
@Service
public class TokenStrategySwitcher {

    private final TokenOverflowStrategyFactory strategyFactory;

    public TokenStrategySwitcher(TokenOverflowStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    /**
     * 根据策略类型执行Token处理
     *
     * @param strategyType 策略类型
     * @param messages 待处理的消息列表
     * @param config 策略配置
     * @return 处理结果
     */
    public TokenOverflowResult executeStrategy(OverflowStrategy strategyType,
                                        List<TokenUsageDTO> messages,
                                        TokenOverflowConfig config) {
        TokenOverflowStrategy strategy = TokenOverflowStrategyFactory.createStrategy(strategyType, config);
        return strategy.process(messages, config);
    }

    /**
     * 根据字符串策略类型执行Token处理
     *
     * @param strategyName 策略名称字符串
     * @param messages 待处理的消息列表
     * @param config 策略配置
     * @return 处理结果
     */
    public TokenOverflowResult executeStrategy(String strategyName,
                                        List<TokenUsageDTO> messages,
                                        TokenOverflowConfig config) {
        TokenOverflowStrategy strategy = TokenOverflowStrategyFactory.createStrategy(strategyName, config);
        return strategy.process(messages, config);
    }

    /**
     * 智能切换策略（根据消息数量和Token使用情况自动选择）
     *
     * @param messages 待处理的消息列表
     * @param maxTokens 最大Token限制
     * @return 处理结果（自动选择最合适的策略）
     */
    public TokenOverflowResult smartSwitch(List<TokenUsageDTO> messages, int maxTokens) {
        // 计算当前Token总数
        int currentTokens = messages.stream()
                .mapToInt(dto -> dto.getBodyTokenCount() != null ? dto.getBodyTokenCount() : 0)
                .sum();

        // 根据条件自动选择策略
        TokenOverflowConfig config = new TokenOverflowConfig();

        if (messages.size() > 30) {
            // 消息数量过多，使用摘要策略
            config.setStrategyType("SUMMARY");
            config.setSummaryThreshold(20);
            config.setMaxTokens(maxTokens);
        } else if (currentTokens > maxTokens * 0.8) {
            // Token使用接近上限，使用滑动窗口
            config.setStrategyType("SLIDING_WINDOW");
            config.setMaxTokens(maxTokens);
            config.setReserveRatio(0.1);
        } else {
            // 使用默认策略（不处理）
            config.setStrategyType("TRUNCATION");
        }

        return executeStrategy(config.getStrategyType(), messages, config);
    }

    /**
     * 批量测试不同策略的效果
     *
     * @param messages 待处理的消息列表
     * @param config 基础配置
     * @return 各策略的处理结果对比
     */
    public StrategyComparison compareStrategies(List<TokenUsageDTO> messages,
                                               TokenOverflowConfig config) {
        StrategyComparison comparison = new StrategyComparison();

        // 测试截断策略
        TokenOverflowConfig truncationConfig = new TokenOverflowConfig();
        truncationConfig.setStrategyType("TRUNCATION");
        comparison.setTruncationResult(executeStrategy("TRUNCATION", messages, truncationConfig));

        // 测试滑动窗口策略
        TokenOverflowConfig slidingConfig = new TokenOverflowConfig();
        slidingConfig.setStrategyType("SLIDING_WINDOW");
        slidingConfig.setMaxTokens(config.getMaxTokens() != null ? config.getMaxTokens() : 4096);
        slidingConfig.setReserveRatio(config.getReserveRatio() != null ? config.getReserveRatio() : 0.1);
        comparison.setSlidingWindowResult(executeStrategy("SLIDING_WINDOW", messages, slidingConfig));

        // 测试摘要策略
        TokenOverflowConfig summaryConfig = new TokenOverflowConfig();
        summaryConfig.setStrategyType("SUMMARY");
        summaryConfig.setMaxTokens(config.getMaxTokens() != null ? config.getMaxTokens() : 4096);
        summaryConfig.setSummaryThreshold(config.getSummaryThreshold() != null ? config.getSummaryThreshold() : 20);
        comparison.setSummaryResult(executeStrategy("SUMMARY", messages, summaryConfig));

        return comparison;
    }

    /**
     * 策略切换建议
     *
     * @param messages 消息列表
     * @param currentStrategy 当前策略
     * @param maxTokens 最大Token数
     * @return 建议的新策略，如果不需要切换则返回null
     */
    public OverflowStrategy suggestStrategySwitch(List<TokenUsageDTO> messages,
                                                 OverflowStrategy currentStrategy,
                                                 int maxTokens) {
        int messageCount = messages.size();
        int currentTokens = messages.stream()
                .mapToInt(dto -> dto.getBodyTokenCount() != null ? dto.getBodyTokenCount() : 0)
                .sum();

        // 消息数量过多，建议使用摘要策略
        if (messageCount > 50 && currentStrategy != OverflowStrategy.SUMMARY) {
            return OverflowStrategy.SUMMARY;
        }

        // Token使用率过高，建议使用滑动窗口
        if (currentTokens > maxTokens * 0.9 && currentStrategy != OverflowStrategy.SLIDING_WINDOW) {
            return OverflowStrategy.SLIDING_WINDOW;
        }

        // 其他情况保持当前策略
        return null;
    }

    /**
     * 策略对比结果
     */
    public static class StrategyComparison {
        private TokenOverflowResult truncationResult;
        private TokenOverflowResult slidingWindowResult;
        private TokenOverflowResult summaryResult;

        // Getters and Setters
        public TokenOverflowResult getTruncationResult() {
            return truncationResult;
        }

        public void setTruncationResult(TokenOverflowResult truncationResult) {
            this.truncationResult = truncationResult;
        }

        public TokenOverflowResult getSlidingWindowResult() {
            return slidingWindowResult;
        }

        public void setSlidingWindowResult(TokenOverflowResult slidingWindowResult) {
            this.slidingWindowResult = slidingWindowResult;
        }

        public TokenOverflowResult getSummaryResult() {
            return summaryResult;
        }

        public void setSummaryResult(TokenOverflowResult summaryResult) {
            this.summaryResult = summaryResult;
        }

        /**
         * 获取最佳策略建议
         */
        public OverflowStrategy getRecommendedStrategy() {
            if (summaryResult != null && summaryResult.getTotalTokens() < 1000) {
                return OverflowStrategy.SUMMARY;
            } else if (slidingWindowResult != null && slidingWindowResult.getTotalTokens() < 2000) {
                return OverflowStrategy.SLIDING_WINDOW;
            } else {
                return OverflowStrategy.TRUNCATION;
            }
        }
    }
}
