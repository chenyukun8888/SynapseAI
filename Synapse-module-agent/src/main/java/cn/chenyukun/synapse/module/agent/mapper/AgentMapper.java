package cn.chenyukun.synapse.module.agent.mapper;

import cn.chenyukun.synapse.module.agent.model.entity.AgentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Agent Mapper接口
 */
@Mapper
public interface AgentMapper extends BaseMapper<AgentEntity> {

    /**
     * 根据用户ID查询Agent列表
     */
    List<AgentEntity> selectByUserId(@Param("userId") String userId);

    /**
     * 根据用户ID和名称查询Agent列表
     */
    List<AgentEntity> selectByUserIdAndName(@Param("userId") String userId, @Param("name") String name);

    /**
     * 根据用户ID查询工作区Agent列表
     */
    List<AgentEntity> selectWorkspaceAgentsByUserId(@Param("userId") String userId);

    /**
     * 查询已发布的Agent列表
     */
    List<AgentEntity> selectPublishedAgents(@Param("name") String name);
}

