package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.enums.OverflowStrategy;
import cn.chenyukun.synapse.module.llm.service.impl.SlidingWindowStrategy;
import cn.chenyukun.synapse.module.llm.service.impl.SummaryStrategy;
import cn.chenyukun.synapse.module.llm.service.impl.TruncationStrategy;
import org.springframework.stereotype.Service;

/**
 * Token溢出处理策略工厂类
 * 根据策略类型创建对应的策略实例
 */
@Service
public class TokenOverflowStrategyFactory {

    /**
     * 根据策略类型创建对应的策略实例
     *
     * @param strategyType 策略类型
     * @param config 策略配置
     * @return 策略实例
     */
    public static TokenOverflowStrategy createStrategy(OverflowStrategy strategyType,
            TokenOverflowConfig config) {
        if (strategyType == null) {
            return new TruncationStrategy();
        }

        switch (strategyType) {
            case SLIDING_WINDOW:
                return new SlidingWindowStrategy(config);
            case SUMMARY:
                return new SummaryStrategy(config);
            case TRUNCATION:
            default:
                return new TruncationStrategy();
        }
    }

    /**
     * 根据字符串策略类型创建对应的策略实例
     *
     * @param strategyType 策略类型字符串
     * @param config 策略配置
     * @return 策略实例
     */
    public static TokenOverflowStrategy createStrategy(String strategyType,
            TokenOverflowConfig config) {
        OverflowStrategy enumType = OverflowStrategy.fromString(strategyType);
        return createStrategy(enumType, config);
    }


    /**
     * 根据配置创建对应的策略实例
     *
     * @param config 策略配置
     * @return 策略实例
     */
    public static TokenOverflowStrategy createStrategy(TokenOverflowConfig config) {
        if (config == null) {
            return new TruncationStrategy();
        }

        return createStrategy(config.getStrategyType(), config);
    }
}
