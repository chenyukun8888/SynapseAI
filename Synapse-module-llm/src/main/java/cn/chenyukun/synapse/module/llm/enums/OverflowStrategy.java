package cn.chenyukun.synapse.module.llm.enums;

/**
 * Token溢出处理策略枚举
 */
public enum OverflowStrategy {
    /** 截断策略 - 不做任何处理，可能导致超限错误 */
    TRUNCATION,

    /** 滑动窗口策略 - 自动移除旧消息，保留最新内容 */
    SLIDING_WINDOW,

    /** 摘要策略 - 将旧消息转换为摘要，保留关键信息 */
    SUMMARY;

    /**
     * 判断给定字符串是否为有效的枚举值
     *
     * @param value 策略名称字符串
     * @return 是否为有效的策略枚举
     */
    public static boolean isValid(String value) {
        try {
            OverflowStrategy.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从字符串转换为枚举值，如果不存在则返回默认值TRUNCATION
     *
     * @param value 策略名称字符串
     * @return 对应的策略枚举值，如果不匹配则返回TRUNCATION
     */
    public static OverflowStrategy fromString(String value) {
        if (value == null) {
            return TRUNCATION;
        }

        try {
            return OverflowStrategy.valueOf(value);
        } catch (IllegalArgumentException e) {
            return TRUNCATION;
        }
    }
}
