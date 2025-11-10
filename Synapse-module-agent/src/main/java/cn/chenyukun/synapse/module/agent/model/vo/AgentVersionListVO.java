package cn.chenyukun.synapse.module.agent.model.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Agent版本列表响应VO
 */
public class AgentVersionListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String agentId;
    private String currentVersion;
    private List<AgentVersionItemVO> versions;

    public AgentVersionListVO() {
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public List<AgentVersionItemVO> getVersions() {
        return versions;
    }

    public void setVersions(List<AgentVersionItemVO> versions) {
        this.versions = versions;
    }
}

