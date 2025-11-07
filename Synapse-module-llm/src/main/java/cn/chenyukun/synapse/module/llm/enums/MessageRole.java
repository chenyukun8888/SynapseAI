package cn.chenyukun.synapse.module.llm.enums;

/**
 * 消息角色枚举
 */
public enum MessageRole {
    
    USER("user", "用户"),
    ASSISTANT("assistant", "助手"),
    SYSTEM("system", "系统");
    
    private final String code;
    private final String desc;
    
    MessageRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
}

