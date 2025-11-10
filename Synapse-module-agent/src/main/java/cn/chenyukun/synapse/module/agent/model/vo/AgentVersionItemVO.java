package cn.chenyukun.synapse.module.agent.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Agent版本项VO
 */
public class AgentVersionItemVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String versionId;
    private String version;
    private String changelog;
    private String publishStatus;
    private LocalDateTime publishTime;
    private LocalDateTime createdAt;

    public AgentVersionItemVO() {
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public LocalDateTime getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

