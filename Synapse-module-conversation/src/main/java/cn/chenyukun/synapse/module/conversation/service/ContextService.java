package cn.chenyukun.synapse.module.conversation.service;

import java.util.List;

/**
 * 上下文服务接口
 * 负责会话上下文的管理和Token溢出处理
 */
public interface ContextService {

    /**
     * 处理会话上下文
     *
     * @param sessionId 会话ID
     * @param maxTokens 最大token数
     * @param strategyType 策略类型
     * @param summaryThreshold 摘要阈值
     * @return 处理后的上下文信息
     */
    ContextResult processContext(String sessionId, int maxTokens, String strategyType, int summaryThreshold);

    /**
     * 上下文处理结果
     */
    class ContextResult {
        private final Object contextEntity;
        private final List<Object> messageEntities;

        public ContextResult(Object contextEntity, List<Object> messageEntities) {
            this.contextEntity = contextEntity;
            this.messageEntities = messageEntities;
        }

        public Object getContextEntity() {
            return contextEntity;
        }

        public List<Object> getMessageEntities() {
            return messageEntities;
        }
    }
}
