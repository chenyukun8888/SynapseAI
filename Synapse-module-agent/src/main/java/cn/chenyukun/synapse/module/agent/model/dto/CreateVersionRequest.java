package cn.chenyukun.synapse.module.agent.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 创建Agent版本请求 DTO
 */
public class CreateVersionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 版本号，格式: x.y.z
     */
    @NotBlank(message = "版本号不能为空")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "版本号格式错误，应为 x.y.z")
    private String version;

    /**
     * 更新日志
     */
    private String changelog;

    public CreateVersionRequest() {
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
}

