package cn.chenyukun.synapse.infrastructure.external.llm;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * LLM 客户端配置
 */
@Configuration
@ConfigurationProperties(prefix = "llm.siliconflow")
public class LlmClientConfig {
    
    private String apiUrl;
    private String apiKey;
    private String defaultModel;
    private Integer timeout = 60000;
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getDefaultModel() {
        return defaultModel;
    }
    
    public void setDefaultModel(String defaultModel) {
        this.defaultModel = defaultModel;
    }
    
    public Integer getTimeout() {
        return timeout;
    }
    
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}

