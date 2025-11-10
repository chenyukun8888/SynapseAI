package cn.chenyukun.synapse.module.agent.mapper;

import cn.chenyukun.synapse.module.agent.model.entity.AgentVersionEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Agent版本 Mapper接口
 */
@Mapper
public interface AgentVersionMapper extends BaseMapper<AgentVersionEntity> {

    /**
     * 根据Agent ID查询版本列表
     */
    List<AgentVersionEntity> selectByAgentId(@Param("agentId") String agentId);

    /**
     * 根据Agent ID和版本号查询版本
     */
    AgentVersionEntity selectByAgentIdAndVersionNumber(@Param("agentId") String agentId, @Param("versionNumber") String versionNumber);

    /**
     * 根据Agent ID查询最新版本
     */
    AgentVersionEntity selectLatestByAgentId(@Param("agentId") String agentId);

    /**
     * 查询已发布的版本列表
     */
    List<AgentVersionEntity> selectPublishedVersions(@Param("name") String name);
}

