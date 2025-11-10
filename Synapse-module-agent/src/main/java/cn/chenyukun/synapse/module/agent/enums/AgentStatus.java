package cn.chenyukun.synapse.module.agent.enums;

/**
 * Agent状态枚举
 * 0-私有, 1-待审核, 2-已上架, 3-被拒绝, 4-已下架
 */
public enum AgentStatus {
    
    PRIVATE(0, "私有"),
    PENDING_REVIEW(1, "待审核"),
    PUBLISHED(2, "已上架"),
    REJECTED(3, "被拒绝"),
    REMOVED(4, "已下架");
    
    private final Integer code;
    private final String desc;
    
    AgentStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static AgentStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AgentStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

