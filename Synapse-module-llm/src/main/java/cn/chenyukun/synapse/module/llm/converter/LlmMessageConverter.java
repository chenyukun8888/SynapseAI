package cn.chenyukun.synapse.module.llm.converter;

import cn.chenyukun.synapse.module.llm.model.entity.LlmMessageEntity;
import cn.chenyukun.synapse.module.llm.model.vo.MessageVO;

import java.text.SimpleDateFormat;

/**
 * LLM 消息转换器
 */
public class LlmMessageConverter {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    /**
     * Entity -> VO
     */
    public static MessageVO toVO(LlmMessageEntity entity) {
        if (entity == null) {
            return null;
        }
        
        MessageVO vo = new MessageVO();
        vo.setId(entity.getMessageId());
        vo.setRole(entity.getRole());
        vo.setContent(entity.getContent());
        vo.setModel(entity.getModel());
        vo.setTokensUsed(entity.getTokensUsed());
        vo.setCreatedAt(entity.getCreatedAt() != null ? DATE_FORMAT.format(entity.getCreatedAt()) : null);
        
        return vo;
    }
}

