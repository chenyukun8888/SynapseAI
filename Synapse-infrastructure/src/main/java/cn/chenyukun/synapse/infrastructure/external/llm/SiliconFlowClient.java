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
import java.io.IOException;

/**
 * SiliconFlow LLM 客户端
 */
@Component
public class SiliconFlowClient {
    
    private final Logger logger = LoggerFactory.getLogger(SiliconFlowClient.class);
    
    @Resource
    private LlmClientConfig config;
    
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
}

