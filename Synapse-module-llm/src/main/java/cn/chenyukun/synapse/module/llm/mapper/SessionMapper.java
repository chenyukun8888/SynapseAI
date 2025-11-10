package cn.chenyukun.synapse.module.llm.mapper;

import cn.chenyukun.synapse.module.llm.model.entity.SessionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 会话 Mapper
 */
@Mapper
public interface SessionMapper extends BaseMapper<SessionEntity> {

    /**
     * 根据用户ID获取会话列表，按更新时间倒序
     * @param userId 用户ID
     * @param isArchived 是否归档
     * @return 会话列表
     */
    @Select("SELECT * FROM sessions WHERE user_id = #{userId} AND is_archived = #{isArchived} ORDER BY updated_at DESC")
    List<SessionEntity> selectByUserIdOrderByUpdatedAt(@Param("userId") String userId, @Param("isArchived") Boolean isArchived);

    /**
     * 根据用户ID获取会话列表，按创建时间倒序
     * @param userId 用户ID
     * @param isArchived 是否归档
     * @return 会话列表
     */
    @Select("SELECT * FROM sessions WHERE user_id = #{userId} AND is_archived = #{isArchived} ORDER BY created_at DESC")
    List<SessionEntity> selectByUserIdOrderByCreatedAt(@Param("userId") String userId, @Param("isArchived") Boolean isArchived);

    /**
     * 统计用户的会话数量
     * @param userId 用户ID
     * @param isArchived 是否归档
     * @return 会话数量
     */
    @Select("SELECT COUNT(*) FROM sessions WHERE user_id = #{userId} AND is_archived = #{isArchived}")
    long countByUserId(@Param("userId") String userId, @Param("isArchived") Boolean isArchived);
}

