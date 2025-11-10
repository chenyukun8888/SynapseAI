package cn.chenyukun.synapse.module.agent.enums;

/**
 * Agent类型枚举
 */
public enum AgentType {
    
    CHAT_ASSISTANT(1, "聊天助手"),
    FUNCTIONAL_AGENT(2, "功能性Agent");
    
    private final Integer code;
    private final String desc;
    
    AgentType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static AgentType fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AgentType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

