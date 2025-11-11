package cn.chenyukun.synapse.module.llm.mapper;

import cn.chenyukun.synapse.module.llm.model.entity.TokenUsageRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Token使用记录Mapper接口
 */
@Mapper
public interface TokenUsageRecordMapper extends BaseMapper<TokenUsageRecordEntity> {

    /**
     * 根据会话ID查询Token使用记录
     *
     * @param sessionId 会话ID
     * @return Token使用记录列表
     */
    @Select("SELECT * FROM token_usage_records WHERE session_id = #{sessionId} ORDER BY created_at DESC")
    List<TokenUsageRecordEntity> selectBySessionId(@Param("sessionId") String sessionId);

    /**
     * 根据用户ID查询Token使用记录
     *
     * @param userId 用户ID
     * @return Token使用记录列表
     */
    @Select("SELECT * FROM token_usage_records WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<TokenUsageRecordEntity> selectByUserId(@Param("userId") String userId);

    /**
     * 统计时间范围内的Token使用总量
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return Token使用总量
     */
    @Select("SELECT SUM(total_tokens) FROM token_usage_records " +
            "WHERE user_id = #{userId} AND created_at BETWEEN #{startTime} AND #{endTime}")
    Integer sumTotalTokensByUserIdAndTimeRange(@Param("userId") String userId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 统计会话的Token使用总量
     *
     * @param sessionId 会话ID
     * @return Token使用总量
     */
    @Select("SELECT SUM(total_tokens) FROM token_usage_records WHERE session_id = #{sessionId}")
    Integer sumTotalTokensBySessionId(@Param("sessionId") String sessionId);

    /**
     * 删除指定时间之前的记录
     *
     * @param beforeTime 时间点
     * @return 删除的记录数
     */
    int deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);
}
