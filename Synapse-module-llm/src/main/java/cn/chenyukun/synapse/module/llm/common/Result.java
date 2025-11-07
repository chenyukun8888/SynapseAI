package cn.chenyukun.synapse.module.llm.common;

/**
 * 统一响应结果
 */
public class Result<T> {
    
    private Integer code;
    private String message;
    private T data;
    
    public Result() {
    }
    
    public Result(Integer code, String message) {
        this(code, message, null);
    }
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Result<T> success() {
        return new Result<>(200, "success");
    }
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }
    
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message);
    }
    
    public static <T> Result<T> badRequest(String message) {
        return error(400, message);
    }
    
    public static <T> Result<T> serverError(String message) {
        return error(500, message);
    }
    
    // Getters and Setters
    
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
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}

