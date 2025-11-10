package cn.chenyukun.synapse.module.llm.mapper;

import cn.chenyukun.synapse.module.llm.model.entity.MessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 消息 Mapper
 */
@Mapper
public interface MessageMapper extends BaseMapper<MessageEntity> {

    /**
     * 根据会话ID获取消息列表，按创建时间升序
     * @param sessionId 会话ID
     * @return 消息列表
     */
    @Select("SELECT * FROM messages WHERE session_id = #{sessionId} ORDER BY created_at ASC")
    List<MessageEntity> selectBySessionIdOrderByCreatedAt(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID统计消息数量
     * @param sessionId 会话ID
     * @return 消息数量
     */
    @Select("SELECT COUNT(*) FROM messages WHERE session_id = #{sessionId}")
    long countBySessionId(@Param("sessionId") String sessionId);

    /**
     * 获取会话的最后一条消息
     * @param sessionId 会话ID
     * @return 最后一条消息
     */
    @Select("SELECT * FROM messages WHERE session_id = #{sessionId} ORDER BY created_at DESC LIMIT 1")
    MessageEntity selectLastMessageBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据会话ID和角色统计Token总数
     * @param sessionId 会话ID
     * @param role 角色
     * @return Token总数
     */
    @Select("SELECT COALESCE(SUM(token_count), 0) FROM messages WHERE session_id = #{sessionId} AND role = #{role}")
    long sumTokensBySessionIdAndRole(@Param("sessionId") String sessionId, @Param("role") String role);

    /**
     * 根据会话ID统计总Token数
     * @param sessionId 会话ID
     * @return 总Token数
     */
    @Select("SELECT COALESCE(SUM(token_count), 0) FROM messages WHERE session_id = #{sessionId}")
    long sumTokensBySessionId(@Param("sessionId") String sessionId);

    /**
     * 删除会话的所有消息
     * @param sessionId 会话ID
     * @return 删除的消息数量
     */
    @Select("DELETE FROM messages WHERE session_id = #{sessionId}")
    int deleteBySessionId(@Param("sessionId") String sessionId);
}

