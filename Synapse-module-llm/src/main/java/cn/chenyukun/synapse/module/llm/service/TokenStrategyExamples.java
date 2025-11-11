package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.enums.OverflowStrategy;
import cn.chenyukun.synapse.module.llm.model.dto.TokenOverflowResult;
import cn.chenyukun.synapse.module.llm.model.dto.TokenUsageDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Token策略切换使用示例
 * 演示如何在代码中切换和使用不同的Token处理策略
 */
@Service
public class TokenStrategyExamples {

    private final TokenStrategySwitcher strategySwitcher;

    public TokenStrategyExamples(TokenStrategySwitcher strategySwitcher) {
        this.strategySwitcher = strategySwitcher;
    }

    /**
     * 示例1：手动切换策略
     */
    public void exampleManualStrategySwitch() {
        // 创建测试消息
        List<TokenUsageDTO> messages = createSampleMessages(10);

        // 配置1：使用滑动窗口策略
        TokenOverflowConfig slidingConfig = new TokenOverflowConfig();
        slidingConfig.setStrategyType("SLIDING_WINDOW");
        slidingConfig.setMaxTokens(300);
        slidingConfig.setReserveRatio(0.1);

        TokenOverflowResult slidingResult =
                strategySwitcher.executeStrategy("SLIDING_WINDOW", messages, slidingConfig);
        System.out.println("滑动窗口策略结果：保留" + slidingResult.getRetainedMessages().size() + "条消息");

        // 配置2：切换到摘要策略
        TokenOverflowConfig summaryConfig = new TokenOverflowConfig();
        summaryConfig.setStrategyType("SUMMARY");
        summaryConfig.setMaxTokens(400);
        summaryConfig.setSummaryThreshold(5);

        TokenOverflowResult summaryResult =
                strategySwitcher.executeStrategy("SUMMARY", messages, summaryConfig);
        System.out.println("摘要策略结果：保留" + summaryResult.getRetainedMessages().size() + "条消息");
        System.out.println("摘要内容：" + summaryResult.getSummary());
    }

    /**
     * 示例2：运行时动态切换策略
     */
    public void exampleRuntimeStrategySwitch() {
        List<TokenUsageDTO> messages = createSampleMessages(25);

        // 根据消息数量动态选择策略
        if (messages.size() > 20) {
            // 大量消息，使用摘要策略
            TokenOverflowConfig config = new TokenOverflowConfig();
            config.setStrategyType("SUMMARY");
            config.setSummaryThreshold(15);

            TokenOverflowResult result =
                    strategySwitcher.executeStrategy(OverflowStrategy.SUMMARY, messages, config);
            System.out.println("运行时切换到摘要策略：生成摘要，保留最新消息");
        } else {
            // 少量消息，使用滑动窗口
            TokenOverflowConfig config = new TokenOverflowConfig();
            config.setStrategyType("SLIDING_WINDOW");
            config.setMaxTokens(500);

            TokenOverflowResult result =
                    strategySwitcher.executeStrategy(OverflowStrategy.SLIDING_WINDOW, messages, config);
            System.out.println("运行时切换到滑动窗口策略：保留最新消息");
        }
    }

    /**
     * 示例3：智能策略选择
     */
    public void exampleSmartStrategySelection() {
        List<TokenUsageDTO> messages = createSampleMessages(35);

        // 智能选择策略
        TokenOverflowResult result =
                strategySwitcher.smartSwitch(messages, 1000);

        System.out.println("智能选择策略：" + result.getStrategyName());
        System.out.println("处理后Token数：" + result.getTotalTokens());
        System.out.println("是否进行了处理：" + result.isProcessed());
    }

    /**
     * 示例4：策略对比和建议
     */
    public void exampleStrategyComparison() {
        List<TokenUsageDTO> messages = createSampleMessages(40);

        // 基础配置
        TokenOverflowConfig config = new TokenOverflowConfig();
        config.setMaxTokens(800);
        config.setSummaryThreshold(20);
        config.setReserveRatio(0.1);

        // 对比不同策略的效果
        TokenStrategySwitcher.StrategyComparison comparison =
                strategySwitcher.compareStrategies(messages, config);

        System.out.println("策略对比结果：");
        System.out.println("截断策略 - Token数：" + comparison.getTruncationResult().getTotalTokens());
        System.out.println("滑动窗口 - Token数：" + comparison.getSlidingWindowResult().getTotalTokens());
        System.out.println("摘要策略 - Token数：" + comparison.getSummaryResult().getTotalTokens());

        // 获取建议
        OverflowStrategy recommended = comparison.getRecommendedStrategy();
        System.out.println("推荐策略：" + recommended);

        // 获取切换建议
        OverflowStrategy suggestedSwitch = strategySwitcher.suggestStrategySwitch(
                messages, OverflowStrategy.TRUNCATION, 800);
        if (suggestedSwitch != null) {
            System.out.println("建议切换到：" + suggestedSwitch);
        } else {
            System.out.println("当前策略合适，无需切换");
        }
    }

    /**
     * 示例5：基于配置的策略切换
     */
    public void exampleConfigBasedSwitching() {
        List<TokenUsageDTO> messages = createSampleMessages(15);

        // 从配置文件或数据库读取策略配置
        String strategyFromConfig = "SLIDING_WINDOW"; // 假设从配置读取
        int maxTokensFromConfig = 600;               // 假设从配置读取

        // 根据配置创建策略
        TokenOverflowConfig config = new TokenOverflowConfig();
        config.setStrategyType(strategyFromConfig);
        config.setMaxTokens(maxTokensFromConfig);
        config.setReserveRatio(0.15);

        TokenOverflowResult result =
                strategySwitcher.executeStrategy(strategyFromConfig, messages, config);

        System.out.println("基于配置的策略执行：" + result.getStrategyName());
        System.out.println("配置参数 - 最大Token：" + maxTokensFromConfig);
    }

    /**
     * 创建示例消息
     */
    private List<TokenUsageDTO> createSampleMessages(int count) {
        List<TokenUsageDTO> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            TokenUsageDTO message = new TokenUsageDTO();
            message.setId("msg-" + i);
            message.setContent("这是第" + i + "条测试消息，包含一些内容用于测试Token计算。");
            message.setRole(i % 2 == 0 ? "user" : "assistant");
            message.setTokenCount(50 + (i % 50)); // 模拟不同的Token数
            message.setBodyTokenCount(45 + (i % 45));
            message.setCreatedAt(LocalDateTime.now().minusMinutes(count - i));
            messages.add(message);
        }
        return messages;
    }
}
