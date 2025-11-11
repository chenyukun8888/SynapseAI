package cn.chenyukun.synapse.module.llm.service;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.model.dto.TokenOverflowResult;
import cn.chenyukun.synapse.module.llm.model.dto.TokenUsageDTO;

import java.util.List;

/**
 * Token溢出处理策略接口
 * 定义统一的Token溢出处理规范
 */
public interface TokenOverflowStrategy {

    /**
     * 处理消息列表
     *
     * @param messages 待处理的消息列表
     * @param config Token配置
     * @return 处理结果，包含处理后的消息列表、摘要等信息
     */
    TokenOverflowResult process(List<TokenUsageDTO> messages, TokenOverflowConfig config);

    /**
     * 获取策略名称
     *
     * @return 策略名称
     */
    String getStrategyName();

    /**
     * 检查是否需要处理
     *
     * @param messages 待检查的消息列表
     * @param config Token配置
     * @return 是否需要处理
     */
    boolean needsProcessing(List<TokenUsageDTO> messages, TokenOverflowConfig config);
}
