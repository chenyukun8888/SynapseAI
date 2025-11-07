package cn.chenyukun.synapse.infrastructure.external.llm;

import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmRequest;
import cn.chenyukun.synapse.infrastructure.external.llm.dto.LlmResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * SiliconFlow LLM 客户端
 */
@Component
public class SiliconFlowClient {
    
    private final Logger logger = LoggerFactory.getLogger(SiliconFlowClient.class);
    
    @Resource
    private LlmClientConfig config;
    
    /**
     * 流式数据回调接口
     */
    public interface StreamCallback {
        /**
         * 处理流式数据块
         * @param content 内容分片
         * @param isDone 是否完成
         */
        void onChunk(String content, boolean isDone);
        
        /**
         * 处理错误
         * @param error 错误信息
         */
        void onError(String error);
        
        /**
         * 完成时的 token 使用统计
         * @param usage token 使用情况
         */
        void onComplete(LlmResponse.TokenUsage usage);
    }
    
    /**
     * 发送聊天请求
     */
    public LlmResponse chat(LlmRequest request) {
        // 检查 API Key
        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
            logger.error("SiliconFlow API Key 未配置！请在 application-dev.yml 中配置 llm.siliconflow.api-key");
            LlmResponse errorResponse = new LlmResponse("API Key 未配置，请在配置文件中设置 llm.siliconflow.api-key");
            errorResponse.setProvider("SiliconFlow");
            return errorResponse;
        }
        
        if (request.getModel() == null || request.getModel().isEmpty()) {
            request.setModel(config.getDefaultModel());
        }
        
        try {
            logger.info("发送请求到 SiliconFlow, 模型: {}, 消息数: {}, API URL: {}", 
                    request.getModel(), request.getMessages().size(), config.getApiUrl());
            
            String requestBody = prepareRequestBody(request);
            logger.debug("请求体: {}", requestBody);
            
            String responseBody = sendHttpRequest(requestBody);
            
            logger.debug("SiliconFlow 响应: {}", responseBody);
            return parseResponse(responseBody, request.getModel());
        } catch (Exception e) {
            logger.error("调用 SiliconFlow 服务出错", e);
            LlmResponse errorResponse = new LlmResponse("调用服务时发生错误: " + e.getMessage());
            errorResponse.setProvider("SiliconFlow");
            errorResponse.setModel(request.getModel());
            return errorResponse;
        }
    }
    
    /**
     * 准备请求体
     */
    private String prepareRequestBody(LlmRequest request) {
        JSONObject requestJson = new JSONObject();
        requestJson.put("model", request.getModel());
        
        JSONArray messagesJson = new JSONArray();
        for (LlmRequest.LlmMessage message : request.getMessages()) {
            JSONObject messageJson = new JSONObject();
            messageJson.put("role", message.getRole());
            messageJson.put("content", message.getContent());
            messagesJson.add(messageJson);
        }
        requestJson.put("messages", messagesJson);
        
        requestJson.put("temperature", request.getTemperature() != null ? request.getTemperature() : 0.7);
        if (request.getMaxTokens() != null) {
            requestJson.put("max_tokens", request.getMaxTokens());
        }
        requestJson.put("stream", request.getStream() != null ? request.getStream() : false);
        
        return requestJson.toJSONString();
    }
    
    /**
     * 解析响应
     */
    private LlmResponse parseResponse(String responseBody, String model) {
        JSONObject responseJson = JSON.parseObject(responseBody);
        
        LlmResponse response = new LlmResponse();
        response.setProvider("SiliconFlow");
        response.setModel(model);
        
        try {
            // 检查是否有错误响应
            if (responseJson.containsKey("code") && responseJson.getInteger("code") != 200) {
                String errorMsg = responseJson.getString("message");
                logger.error("SiliconFlow API 错误: code={}, message={}", 
                        responseJson.getInteger("code"), errorMsg);
                response.setContent("API 调用失败: " + errorMsg);
                return response;
            }
            
            // 解析正常响应
            JSONArray choicesArray = responseJson.getJSONArray("choices");
            if (choicesArray == null || choicesArray.isEmpty()) {
                logger.error("响应中没有 choices 数组");
                response.setContent("API 响应格式错误：缺少 choices");
                return response;
            }
            
            JSONObject choices = choicesArray.getJSONObject(0);
            JSONObject message = choices.getJSONObject("message");
            String content = message.getString("content");
            
            response.setContent(content);
            response.setFinishReason(choices.getString("finish_reason"));
            
            if (responseJson.containsKey("usage")) {
                JSONObject usage = responseJson.getJSONObject("usage");
                LlmResponse.TokenUsage tokenUsage = new LlmResponse.TokenUsage(
                    usage.getInteger("prompt_tokens"),
                    usage.getInteger("completion_tokens"),
                    usage.getInteger("total_tokens")
                );
                response.setTokenUsage(tokenUsage);
                logger.info("Token 使用: input={}, output={}, total={}", 
                        tokenUsage.getInput(), tokenUsage.getOutput(), tokenUsage.getTotal());
            }
            
            return response;
        } catch (Exception e) {
            logger.error("解析 SiliconFlow 响应出错: {}", responseBody, e);
            response.setContent("解析服务响应时发生错误: " + e.getMessage());
            return response;
        }
    }
    
    /**
     * 发送 HTTP 请求
     */
    private String sendHttpRequest(String requestBody) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getTimeout())
                .setSocketTimeout(config.getTimeout())
                .build();
        
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        
        HttpPost httpPost = new HttpPost(config.getApiUrl());
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + config.getApiKey());
        
        StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        
        logger.debug("发送 HTTP 请求到: {}", config.getApiUrl());
        
        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("HTTP 响应状态码: {}", statusCode);
            
            if (statusCode != 200) {
                logger.error("HTTP 请求失败，状态码: {}", statusCode);
            }
            
            HttpEntity responseEntity = response.getEntity();
            return EntityUtils.toString(responseEntity);
        } finally {
            httpClient.close();
        }
    }
    
    /**
     * 发送流式聊天请求
     * 
     * @param request 请求参数
     * @param callback 流式数据回调
     */
    public void chatStream(LlmRequest request, StreamCallback callback) {
        // 检查 API Key
        if (config.getApiKey() == null || config.getApiKey().isEmpty()) {
            logger.error("SiliconFlow API Key 未配置！");
            callback.onError("API Key 未配置，请在配置文件中设置 llm.siliconflow.api-key");
            return;
        }
        
        if (request.getModel() == null || request.getModel().isEmpty()) {
            request.setModel(config.getDefaultModel());
        }
        
        // 强制设置流式模式
        request.setStream(true);
        
        try {
            logger.info("发送流式请求到 SiliconFlow, 模型: {}, 消息数: {}", 
                    request.getModel(), request.getMessages().size());
            
            String requestBody = prepareRequestBody(request);
            logger.debug("流式请求体: {}", requestBody);
            
            sendStreamHttpRequest(requestBody, callback);
            
        } catch (Exception e) {
            logger.error("调用 SiliconFlow 流式服务出错", e);
            callback.onError("调用服务时发生错误: " + e.getMessage());
        }
    }
    
    /**
     * 发送流式 HTTP 请求
     */
    private void sendStreamHttpRequest(String requestBody, StreamCallback callback) throws IOException {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getTimeout())
                .setSocketTimeout(config.getTimeout())
                .build();
        
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
        
        HttpPost httpPost = new HttpPost(config.getApiUrl());
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + config.getApiKey());
        httpPost.setHeader("Accept", "text/event-stream");
        
        StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        
        logger.debug("发送流式 HTTP 请求到: {}", config.getApiUrl());
        
        CloseableHttpResponse response = null;
        BufferedReader reader = null;
        
        try {
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.debug("HTTP 响应状态码: {}", statusCode);
            
            if (statusCode != 200) {
                logger.error("HTTP 请求失败，状态码: {}", statusCode);
                callback.onError("HTTP 请求失败，状态码: " + statusCode);
                return;
            }
            
            HttpEntity responseEntity = response.getEntity();
            reader = new BufferedReader(new InputStreamReader(
                    responseEntity.getContent(), StandardCharsets.UTF_8));
            
            String line;
            StringBuilder dataBuffer = new StringBuilder();
            LlmResponse.TokenUsage finalUsage = null;
            
            while ((line = reader.readLine()) != null) {
                // SSE 格式：data: {...}
                if (line.startsWith("data: ")) {
                    String data = line.substring(6).trim();
                    
                    // 跳过 [DONE] 标记
                    if ("[DONE]".equals(data)) {
                        logger.debug("收到流式结束标记");
                        callback.onChunk("", true);
                        break;
                    }
                    
                    try {
                        JSONObject jsonData = JSON.parseObject(data);
                        
                        // 检查错误
                        if (jsonData.containsKey("code") && jsonData.getInteger("code") != 200) {
                            String errorMsg = jsonData.getString("message");
                            logger.error("SiliconFlow API 错误: {}", errorMsg);
                            callback.onError("API 错误: " + errorMsg);
                            return;
                        }
                        
                        // 解析流式数据
                        JSONArray choices = jsonData.getJSONArray("choices");
                        if (choices != null && !choices.isEmpty()) {
                            JSONObject choice = choices.getJSONObject(0);
                            JSONObject delta = choice.getJSONObject("delta");
                            
                            if (delta != null && delta.containsKey("content")) {
                                String content = delta.getString("content");
                                if (content != null && !content.isEmpty()) {
                                    logger.debug("收到内容分片: {}", content);
                                    callback.onChunk(content, false);
                                }
                            }
                            
                            // 检查是否完成
                            String finishReason = choice.getString("finish_reason");
                            if (finishReason != null && !finishReason.isEmpty()) {
                                logger.debug("流式完成，原因: {}", finishReason);
                                callback.onChunk("", true);
                            }
                        }
                        
                        // 解析 token 使用情况
                        if (jsonData.containsKey("usage")) {
                            JSONObject usage = jsonData.getJSONObject("usage");
                            finalUsage = new LlmResponse.TokenUsage(
                                usage.getInteger("prompt_tokens"),
                                usage.getInteger("completion_tokens"),
                                usage.getInteger("total_tokens")
                            );
                        }
                        
                    } catch (Exception e) {
                        logger.error("解析流式数据出错: {}", data, e);
                    }
                }
            }
            
            // 完成回调
            if (finalUsage != null) {
                logger.info("流式完成，Token 使用: input={}, output={}, total={}", 
                        finalUsage.getInput(), finalUsage.getOutput(), finalUsage.getTotal());
                callback.onComplete(finalUsage);
            } else {
                callback.onComplete(new LlmResponse.TokenUsage(0, 0, 0));
            }
            
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.error("关闭 reader 失败", e);
                }
            }
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    logger.error("关闭 response 失败", e);
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                logger.error("关闭 httpClient 失败", e);
            }
        }
    }
}

