package cn.chenyukun.synapse.module.agent.converter;

import cn.chenyukun.synapse.module.agent.model.entity.AgentEntity;
import cn.chenyukun.synapse.module.agent.model.vo.AgentVO;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Agent转换器
 */
@Component
public class AgentConverter {

    /**
     * Entity转VO
     */
    public AgentVO toVO(AgentEntity entity) {
        if (entity == null) {
            return null;
        }

        AgentVO vo = new AgentVO();
        vo.setAgentId(entity.getId());
        vo.setName(entity.getName());
        vo.setDescription(entity.getDescription());
        vo.setAvatar(entity.getAvatar());
        vo.setSystemPrompt(entity.getSystemPrompt());
        vo.setStatus(entity.getEnabled() ? "ACTIVE" : "ARCHIVED");
        vo.setIsPublic(false); // TODO: 从数据库字段获取
        vo.setVersion("1.0.0"); // TODO: 从版本表获取
        vo.setCreatedAt(entity.getCreatedAt());
        vo.setUpdatedAt(entity.getUpdatedAt());

        // 转换类型
        vo.setType(convertIntegerToType(entity.getAgentType()));

        // 转换模型配置
        Map<String, Object> modelConfig = entity.getModelConfig();
        if (modelConfig != null) {
            vo.setModel((String) modelConfig.get("model"));
            vo.setTemperature(modelConfig.get("temperature") != null ? 
                    ((Number) modelConfig.get("temperature")).doubleValue() : null);
            vo.setMaxTokens(modelConfig.get("maxTokens") != null ? 
                    ((Number) modelConfig.get("maxTokens")).intValue() : null);
        }

        // 转换工具列表
        if (entity.getTools() != null && !entity.getTools().isEmpty()) {
            vo.setTools(entity.getTools().stream()
                    .map(tool -> (String) tool.get("name"))
                    .filter(StrUtil::isNotBlank)
                    .collect(java.util.stream.Collectors.toList()));
        }

        // 设置知识库ID列表
        vo.setKnowledgeBaseIds(entity.getKnowledgeBaseIds());

        return vo;
    }

    /**
     * 转换整数为类型字符串
     */
    private String convertIntegerToType(Integer agentType) {
        if (agentType == null) {
            return "CHAT";
        }
        switch (agentType) {
            case 1:
                return "CHAT";
            case 2:
                return "TASK";
            default:
                return "CHAT";
        }
    }
}

