package cn.chenyukun.synapse.module.llm.service.impl;

import cn.chenyukun.synapse.module.llm.config.TokenOverflowConfig;
import cn.chenyukun.synapse.module.llm.mapper.TokenUsageRecordMapper;
import cn.chenyukun.synapse.module.llm.model.dto.TokenOverflowResult;
import cn.chenyukun.synapse.module.llm.model.dto.TokenUsageDTO;
import cn.chenyukun.synapse.module.llm.model.entity.TokenUsageRecordEntity;
import cn.chenyukun.synapse.module.llm.service.TokenManager;
import cn.chenyukun.synapse.module.llm.service.TokenOverflowStrategy;
import cn.chenyukun.synapse.module.llm.service.TokenOverflowStrategyFactory;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Token管理器实现类
 */
@Service
public class TokenManagerImpl implements TokenManager {

    @Autowired
    private TokenUsageRecordMapper tokenUsageRecordMapper;

    @Override
    public int calculateTotalTokens(List<TokenUsageDTO> messages) {
        return messages.stream()
                .mapToInt(dto -> dto.getBodyTokenCount() != null ? dto.getBodyTokenCount() : 0)
                .sum();
    }

    @Override
    public TokenOverflowResult processTokenOverflow(List<TokenUsageDTO> messages,
                                                   TokenOverflowConfig config) {
        // 使用工厂模式创建策略实例
        TokenOverflowStrategy strategy = TokenOverflowStrategyFactory.createStrategy(config);
        // 执行策略处理
        return strategy.process(messages, config);
    }

    @Override
    public void recordTokenUsage(String sessionId, TokenUsageDTO tokenUsage) {
        if (tokenUsage == null || tokenUsageRecordMapper == null) {
            return;
        }

        try {
            // 创建Token使用记录实体
            TokenUsageRecordEntity record = new TokenUsageRecordEntity();

            // 设置基本信息
            record.setSessionId(sessionId);
            record.setUserId(tokenUsage.getUserId());
            record.setModelName(tokenUsage.getModelName() != null ? tokenUsage.getModelName() : "unknown");

            // 设置Token使用情况
            record.setInputTokens(tokenUsage.getTokenCount() != null ? tokenUsage.getTokenCount() : 0);
            record.setOutputTokens(0); // 如果有输出token可以设置
            record.setTotalTokens(tokenUsage.getTokenCount() != null ? tokenUsage.getTokenCount() : 0);

            // 设置时间
            record.setRequestTime(LocalDateTime.now());
            record.setResponseTime(LocalDateTime.now());

            // 估算处理时间（可以根据实际调用情况调整）
            record.setProcessingTime(100L); // 100ms

            // 设置状态
            record.setSuccess(true);

            // 保存到数据库
            tokenUsageRecordMapper.insert(record);

        } catch (Exception e) {
            // 记录错误但不抛出异常，避免影响主要业务流程
            System.err.println("Failed to record token usage: " + e.getMessage());
        }
    }

    @Override
    public TokenUsageDTO getTokenUsageStats(String sessionId) {
        if (tokenUsageRecordMapper == null || sessionId == null) {
            return createEmptyStats(sessionId);
        }

        try {
            // 查询该会话的所有Token使用记录
            List<TokenUsageRecordEntity> records = tokenUsageRecordMapper.selectBySessionId(sessionId);

            if (records == null || records.isEmpty()) {
                return createEmptyStats(sessionId);
            }

            // 计算统计信息
            TokenUsageDTO stats = new TokenUsageDTO();
            stats.setSessionId(sessionId);
            stats.setRole("SYSTEM"); // 统计信息
            stats.setContent("Token usage statistics for session: " + sessionId);

            // 计算总Token数
            int totalTokens = records.stream()
                    .mapToInt(record -> record.getTotalTokens() != null ? record.getTotalTokens() : 0)
                    .sum();
            stats.setTokenCount(totalTokens);
            stats.setBodyTokenCount(totalTokens);

            // 计算平均处理时间
            double avgProcessingTime = records.stream()
                    .filter(record -> record.getProcessingTime() != null)
                    .mapToLong(TokenUsageRecordEntity::getProcessingTime)
                    .average()
                    .orElse(0.0);

            // 计算请求次数
            int requestCount = records.size();

            // 计算成功率
            long successCount = records.stream()
                    .filter(record -> record.getSuccess() != null && record.getSuccess())
                    .count();

            double successRate = requestCount > 0 ? (double) successCount / requestCount : 0.0;

            // 在content中添加详细统计信息
            StringBuilder details = new StringBuilder();
            details.append("Session Token Statistics:\n");
            details.append("- Total Requests: ").append(requestCount).append("\n");
            details.append("- Total Tokens: ").append(totalTokens).append("\n");
            details.append("- Average Processing Time: ").append(String.format("%.2f", avgProcessingTime)).append("ms\n");
            details.append("- Success Rate: ").append(String.format("%.2f%%", successRate * 100)).append("\n");

            // 按模型分组统计
            records.stream()
                    .filter(record -> record.getModelName() != null)
                    .collect(Collectors.groupingBy(TokenUsageRecordEntity::getModelName))
                    .forEach((model, modelRecords) -> {
                        int modelTokens = modelRecords.stream()
                                .mapToInt(record -> record.getTotalTokens() != null ? record.getTotalTokens() : 0)
                                .sum();
                        details.append("- ").append(model).append(": ").append(modelTokens).append(" tokens\n");
                    });

            stats.setContent(details.toString());

            // 设置时间为最后一次请求时间
            LocalDateTime lastRequestTime = records.stream()
                    .map(TokenUsageRecordEntity::getRequestTime)
                    .max(LocalDateTime::compareTo)
                    .orElse(LocalDateTime.now());
            stats.setCreatedAt(lastRequestTime);

            return stats;

        } catch (Exception e) {
            System.err.println("Failed to get token usage stats: " + e.getMessage());
            return createEmptyStats(sessionId);
        }
    }

    /**
     * 创建空的统计信息
     */
    private TokenUsageDTO createEmptyStats(String sessionId) {
        TokenUsageDTO stats = new TokenUsageDTO();
        stats.setSessionId(sessionId);
        stats.setRole("SYSTEM");
        stats.setContent("No token usage data available for session: " + sessionId);
        stats.setTokenCount(0);
        stats.setBodyTokenCount(0);
        stats.setCreatedAt(LocalDateTime.now());
        return stats;
    }

    @Override
    public boolean isTokenOverflow(int currentTokens, int maxTokens) {
        return currentTokens > maxTokens;
    }
}
