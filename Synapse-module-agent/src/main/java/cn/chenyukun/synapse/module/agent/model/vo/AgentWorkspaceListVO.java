package cn.chenyukun.synapse.module.agent.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Agent工作区列表响应VO
 */
public class AgentWorkspaceListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String agentId;
    private List<AgentWorkspaceVO> workspaces;

    public AgentWorkspaceListVO() {
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public List<AgentWorkspaceVO> getWorkspaces() {
        return workspaces;
    }

    public void setWorkspaces(List<AgentWorkspaceVO> workspaces) {
        this.workspaces = workspaces;
    }
}

