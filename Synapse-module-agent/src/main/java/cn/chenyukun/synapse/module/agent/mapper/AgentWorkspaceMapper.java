package cn.chenyukun.synapse.module.agent.mapper;

import cn.chenyukun.synapse.module.agent.model.entity.AgentWorkspaceEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Agent工作区 Mapper接口
 */
@Mapper
public interface AgentWorkspaceMapper extends BaseMapper<AgentWorkspaceEntity> {

    /**
     * 根据用户ID查询工作区Agent列表
     */
    List<AgentWorkspaceEntity> selectByUserId(@Param("userId") String userId);

    /**
     * 根据Agent ID和用户ID查询工作区记录
     */
    AgentWorkspaceEntity selectByAgentIdAndUserId(@Param("agentId") String agentId, @Param("userId") String userId);
}

