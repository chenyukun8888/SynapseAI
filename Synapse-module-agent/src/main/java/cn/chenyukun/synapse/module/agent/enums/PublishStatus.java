package cn.chenyukun.synapse.module.agent.enums;

/**
 * 发布状态枚举
 * 1-审核中, 2-已发布, 3-拒绝, 4-已下架
 */
public enum PublishStatus {
    
    REVIEWING(1, "审核中"),
    PUBLISHED(2, "已发布"),
    REJECTED(3, "拒绝"),
    REMOVED(4, "已下架");
    
    private final Integer code;
    private final String desc;
    
    PublishStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getDesc() {
        return desc;
    }
    
    public static PublishStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (PublishStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

