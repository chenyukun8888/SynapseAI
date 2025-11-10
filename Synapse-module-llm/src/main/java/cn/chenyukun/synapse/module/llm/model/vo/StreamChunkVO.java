package cn.chenyukun.synapse.module.llm.model.vo;

import com.alibaba.fastjson.JSON;

/**
 * 流式数据块 VO
 */
public class StreamChunkVO {

    /**
     * 事件类型：start/chunk/done/error
     */
    private String type;

    /**
     * 消息唯一ID（start事件）
     */
    private String messageId;

    /**
     * 模型名称（start事件）
     */
    private String model;

    /**
     * 会话ID（start事件）
     */
    private String sessionId;

    /**
     * 文本内容分片（chunk事件）
     */
    private String content;

    /**
     * 分片索引（chunk事件）
     */
    private Integer index;

    /**
     * Token使用统计（done事件）
     */
    private TokenUsage tokensUsed;

    /**
     * 结束原因（done事件）：stop/length/error
     */
    private String finishReason;

    /**
     * 错误码（error事件）
     */
    private Integer code;

    /**
     * 错误消息（error事件）
     */
    private String message;

    /**
     * 时间戳
     */
    private String timestamp;

    public StreamChunkVO() {
    }

    /**
     * 创建 start 事件
     */
    public static StreamChunkVO createStart(String messageId, String model, String sessionId, String timestamp) {
        StreamChunkVO chunk = new StreamChunkVO();
        chunk.type = "start";
        chunk.messageId = messageId;
        chunk.model = model;
        chunk.sessionId = sessionId;
        chunk.timestamp = timestamp;
        return chunk;
    }

    /**
     * 创建 start 事件（简化版本，自动生成时间戳）
     */
    public static StreamChunkVO start(String messageId, String model, String sessionId) {
        return createStart(messageId, model, sessionId, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 创建 chunk 事件
     */
    public static StreamChunkVO createChunk(String content, Integer index) {
        StreamChunkVO chunk = new StreamChunkVO();
        chunk.type = "chunk";
        chunk.content = content;
        chunk.index = index;
        return chunk;
    }

    /**
     * 创建 chunk 事件（简化版本）
     */
    public static StreamChunkVO chunk(String content, Integer index) {
        return createChunk(content, index);
    }

    /**
     * 创建 done 事件
     */
    public static StreamChunkVO createDone(TokenUsage tokensUsed, String finishReason, String timestamp) {
        StreamChunkVO chunk = new StreamChunkVO();
        chunk.type = "done";
        chunk.tokensUsed = tokensUsed;
        chunk.finishReason = finishReason;
        chunk.timestamp = timestamp;
        return chunk;
    }

    /**
     * 创建 done 事件（简化版本，兼容 LlmResponse.TokenUsage）
     */
    public static StreamChunkVO done(cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmResponse.TokenUsage usage, String finishReason) {
        TokenUsage tokenUsage = new TokenUsage(usage.getInput(), usage.getOutput(), usage.getTotal());
        return createDone(tokenUsage, finishReason, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * 创建 error 事件
     */
    public static StreamChunkVO createError(Integer code, String message, String timestamp) {
        StreamChunkVO chunk = new StreamChunkVO();
        chunk.type = "error";
        chunk.code = code;
        chunk.message = message;
        chunk.timestamp = timestamp;
        return chunk;
    }

    /**
     * 创建 error 事件（简化版本）
     */
    public static StreamChunkVO error(Integer code, String message) {
        return createError(code, message, String.valueOf(System.currentTimeMillis()));
    }

    /**
     * Token 使用统计
     */
    public static class TokenUsage {
        private Integer input;
        private Integer output;
        private Integer total;

        public TokenUsage() {
        }

        public TokenUsage(Integer input, Integer output, Integer total) {
            this.input = input;
            this.output = output;
            this.total = total;
        }

        public Integer getInput() {
            return input;
        }

        public void setInput(Integer input) {
            this.input = input;
        }

        public Integer getOutput() {
            return output;
        }

        public void setOutput(Integer output) {
            this.output = output;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
    }

    /**
     * 转换为 JSON 字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }

    // Getters and Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public TokenUsage getTokensUsed() {
        return tokensUsed;
    }

    public void setTokensUsed(TokenUsage tokensUsed) {
        this.tokensUsed = tokensUsed;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}

