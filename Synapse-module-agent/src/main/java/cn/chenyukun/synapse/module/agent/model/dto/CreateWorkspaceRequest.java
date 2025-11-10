package cn.chenyukun.synapse.module.agent.model.dto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * 创建Agent工作区请求 DTO
 */
public class CreateWorkspaceRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 工作区名称
     */
    @NotBlank(message = "工作区名称不能为空")
    private String name;

    /**
     * 工作区配置（根据Agent类型不同）
     */
    private Map<String, Object> config;

    public CreateWorkspaceRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}

