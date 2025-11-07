package cn.chenyukun.synapse.module.llm.mapper;

import cn.chenyukun.synapse.module.llm.model.entity.LlmMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * LLM 消息 Mapper
 */
@Mapper
public interface LlmMessageMapper extends BaseMapper<LlmMessageEntity> {
}

