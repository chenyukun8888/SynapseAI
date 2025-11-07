# AgentX 项目详细版本演进历史

## 📋 目录
- [项目概述](#项目概述)
- [技术栈全景](#技术栈全景)
- [第一阶段：基础对话功能 (2025-03-19)](#第一阶段基础对话功能-2025-03-19)
- [第二阶段：会话管理与 Agent 功能 (2025-03-20 ~ 2025-03-22)](#第二阶段会话管理与-agent-功能-2025-03-20--2025-03-22)
- [后续阶段...](#后续阶段)

---

## 项目概述

**AgentX** 是一个基于大型语言模型（LLM）和多能力平台（MCP）的智能 Agent 构建平台，采用 **DDD（领域驱动设计）架构**。项目从 2025年3月19日 开始，历经 **32 个版本迭代**，从基础的项目初始化逐步发展成为功能完整的 Agent 平台。

### 核心特性
- 🎯 **DDD 架构设计**：清晰的分层架构，易于维护和扩展
- 🤖 **Agent 管理系统**：完整的 Agent 创建、配置、发布流程
- 💬 **多模态对话**：支持文本、图片等多种输入形式
- 🔧 **工具生态**：丰富的工具市场和 MCP 集成
- 🧠 **长期记忆**：Agent 具备记忆能力，实现个性化交互
- 🚀 **高可用部署**：Docker 容器化，一键部署

---

## 技术栈全景

### 后端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 1.8 → 17+ | 核心开发语言 |
| Spring Boot | 2.7.x → 3.x | 应用框架 |
| MyBatis-Plus | 3.5.x | 数据库 ORM |
| PostgreSQL | 14.x+ | 主数据库 |
| pgvector | - | 向量存储扩展 |
| Redis | - | 缓存和会话 |

### 前端技术栈
| 技术 | 版本 | 用途 |
|------|------|------|
| Vue.js | 3.x | 早期前端框架 |
| Next.js | 14+ | 后期前端框架 |
| TypeScript | 5.x | 类型安全 |
| Tailwind CSS | 3.x | UI 样式 |
| Shadcn/ui | - | 组件库 |

### 基础设施
- **容器化**: Docker & Docker Compose
- **网关**: MCP Gateway, API Premium Gateway
- **监控**: 结构化日志 + 追踪系统

---

## DDD 架构分层说明

```
org.xhy
├── interfaces        # 接口层：处理外部请求
│   ├── api          # REST API 接口
│   └── dto          # 数据传输对象
├── application      # 应用层：业务流程编排
│   └── service      # 应用服务
├── domain           # 领域层：核心业务逻辑
│   ├── model        # 领域模型（实体、值对象）
│   ├── repository   # 仓储接口
│   └── service      # 领域服务
└── infrastructure   # 基础设施层：技术实现
    ├── config       # 配置
    ├── integration  # 外部服务集成
    └── persistence  # 数据持久化
```

---

# 第一阶段：基础对话功能 (2025-03-19)

## v1.0 - AgentX-2025-03-19-feat-init

### 📅 版本信息
- **发布日期**: 2025-03-19
- **版本主题**: 项目初始化
- **Java 文件数**: 2
- **开发周期**: 1天

### 🎯 版本目标
建立项目基础框架，完成开发环境搭建，实现最小可用系统（健康检查）。

### 📦 项目架构

```
AgentX/
├── src/main/java/org/xhy/
│   ├── AgentXApplication.java          # Spring Boot 启动类
│   └── interfaces/
│       └── api/
│           └── base/
│               └── HealthController.java  # 健康检查控制器
├── src/main/resources/
│   ├── application.yml                 # 应用配置
│   └── application-dev.yml             # 开发环境配置
└── pom.xml                             # Maven 依赖配置
```

### 🆕 新增内容

#### 1. 核心类文件

**AgentXApplication.java** - 应用启动类
```java
@SpringBootApplication
public class AgentXApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgentXApplication.class, args);
    }
}
```

**HealthController.java** - 健康检查接口
```java
@RestController
@RequestMapping("/health")
public class HealthController {
    @Value("${spring.application.name}")
    private String applicationName;
    
    @GetMapping
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", applicationName);
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
```

#### 2. 配置文件

**application.yml**
```yaml
spring:
  application:
    name: AgentX
  profiles:
    active: dev
server:
  port: 8080
```

**application-dev.yml**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/agentx
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
```

#### 3. Docker 配置

**docker-compose.yml**
```yaml
version: '3.8'
services:
  postgres:
    image: postgres:14
    container_name: agentx-postgres
    environment:
      POSTGRES_DB: agentx
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```

### 💾 数据库内容
- ❌ 本版本未创建任何数据库表
- ✅ 仅完成 PostgreSQL 数据库环境搭建

### 🔧 Maven 依赖

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>2.7.14</version>
    </dependency>
    
    <!-- PostgreSQL Driver -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <version>42.6.0</version>
    </dependency>
</dependencies>
```

### ✅ 功能特性
1. ✅ Spring Boot 项目初始化
2. ✅ 健康检查 API：`GET /health`
3. ✅ PostgreSQL 数据库连接配置
4. ✅ Docker Compose 环境搭建
5. ✅ 多环境配置支持（dev/prod）

### 📝 API 接口

| 方法 | 路径 | 描述 | 返回示例 |
|------|------|------|----------|
| GET | `/health` | 健康检查 | `{"status":"UP","service":"AgentX","timestamp":1710835200000}` |

### 🎓 技术亮点
- 采用 DDD 分层架构设计，为后续扩展打下基础
- 使用 Docker Compose 简化开发环境搭建
- 多环境配置管理，支持开发/生产环境切换

---

## v1.1 - AgentX-2025-03-19-feat-llm-chat

### 📅 版本信息
- **发布日期**: 2025-03-19
- **版本主题**: 实现基础 LLM 对话功能
- **Java 文件数**: 14 (+12)
- **开发周期**: 1天
- **从 v1.0 变更**: 新增 LLM 对话核心功能

### 🎯 版本目标
实现与大语言模型的基础对话能力，完成 DDD 架构的领域模型设计，接入 SiliconFlow LLM 服务。

### 📦 项目架构

```
AgentX/
├── src/main/java/org/xhy/
│   ├── AgentXApplication.java
│   ├── interfaces/                    # 接口层
│   │   └── api/
│   │       ├── base/
│   │       │   └── HealthController.java
│   │       ├── common/
│   │       │   └── Result.java        # 🆕 统一响应封装
│   │       └── conversation/
│   │           └── ConversationController.java  # 🆕 对话控制器
│   ├── application/                   # 🆕 应用层
│   │   └── conversation/
│   │       ├── dto/
│   │       │   ├── ChatRequest.java
│   │       │   └── ChatResponse.java
│   │       └── service/
│   │           └── ConversationService.java
│   ├── domain/                        # 🆕 领域层
│   │   └── llm/
│   │       ├── model/
│   │       │   ├── LlmMessage.java
│   │       │   ├── LlmRequest.java
│   │       │   └── LlmResponse.java
│   │       └── service/
│   │           └── LlmService.java
│   └── infrastructure/                # 🆕 基础设施层
│       ├── config/
│       │   └── LlmConfig.java
│       └── integration/
│           └── llm/
│               ├── AbstractLlmService.java
│               └── siliconflow/
│                   └── SiliconFlowLlmService.java
└── AgentX-frontend/                   # 🆕 前端项目
    ├── src/
    │   ├── components/
    │   │   └── ChatBox.vue
    │   ├── views/
    │   │   └── ChatView.vue
    │   └── main.js
    └── package.json
```

### 🆕 新增内容

#### 1. 领域模型（Domain Model）

**LlmMessage.java** - LLM 消息实体
```java
public class LlmMessage {
    private String role;        // user/assistant/system
    private String content;     // 消息内容
    
    // Constructors, Getters, Setters
}
```

**LlmRequest.java** - LLM 请求实体
```java
public class LlmRequest {
    private String model;                    // 模型名称
    private List<LlmMessage> messages;       // 消息列表
    private Double temperature;              // 温度参数
    private Integer maxTokens;               // 最大 token 数
    private Boolean stream;                  // 是否流式输出
    
    // Builder 模式构造
}
```

**LlmResponse.java** - LLM 响应实体
```java
public class LlmResponse {
    private String id;
    private String model;
    private String content;                  // 响应内容
    private Integer promptTokens;            // 提示词 token 数
    private Integer completionTokens;        // 完成 token 数
    private Integer totalTokens;             // 总 token 数
}
```

#### 2. 领域服务（Domain Service）

**LlmService.java** - LLM 服务接口
```java
public interface LlmService {
    /**
     * 发送聊天请求
     */
    LlmResponse chat(LlmRequest request);
    
    /**
     * 流式聊天（预留接口）
     */
    // Future: stream chat support
}
```

**AbstractLlmService.java** - LLM 服务抽象基类
```java
public abstract class AbstractLlmService implements LlmService {
    
    protected String apiKey;
    protected String baseUrl;
    
    /**
     * 构建请求头
     */
    protected Map<String, String> buildHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        return headers;
    }
    
    /**
     * 子类实现具体的 HTTP 调用
     */
    protected abstract LlmResponse doChat(LlmRequest request);
    
    @Override
    public LlmResponse chat(LlmRequest request) {
        return doChat(request);
    }
}
```

**SiliconFlowLlmService.java** - SiliconFlow 服务实现
```java
@Service
public class SiliconFlowLlmService extends AbstractLlmService {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${llm.siliconflow.api-key}")
    private String apiKey;
    
    @Value("${llm.siliconflow.base-url}")
    private String baseUrl;
    
    @Override
    protected LlmResponse doChat(LlmRequest request) {
        // 1. 构建请求
        String url = baseUrl + "/v1/chat/completions";
        HttpEntity<LlmRequest> entity = new HttpEntity<>(request, buildHeaders());
        
        // 2. 发送请求
        ResponseEntity<Map> response = restTemplate.exchange(
            url, HttpMethod.POST, entity, Map.class
        );
        
        // 3. 解析响应
        return parseResponse(response.getBody());
    }
    
    private LlmResponse parseResponse(Map<String, Object> body) {
        // 解析 SiliconFlow API 响应格式
        // ...
    }
}
```

#### 3. 应用服务（Application Service）

**ConversationService.java** - 对话应用服务
```java
@Service
public class ConversationService {
    
    @Autowired
    private LlmService llmService;
    
    /**
     * 处理用户对话请求
     */
    public ChatResponse chat(ChatRequest request) {
        // 1. 构建 LLM 请求
        LlmRequest llmRequest = LlmRequest.builder()
            .model("Qwen/Qwen2.5-7B-Instruct")
            .messages(Arrays.asList(
                new LlmMessage("user", request.getMessage())
            ))
            .temperature(0.7)
            .maxTokens(2048)
            .build();
        
        // 2. 调用 LLM 服务
        LlmResponse llmResponse = llmService.chat(llmRequest);
        
        // 3. 转换为应用层响应
        ChatResponse response = new ChatResponse();
        response.setMessage(llmResponse.getContent());
        response.setTokens(llmResponse.getTotalTokens());
        
        return response;
    }
}
```

#### 4. 接口层（Interfaces）

**Result.java** - 统一响应封装
```java
public class Result<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }
    
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        return result;
    }
}
```

**ConversationController.java** - 对话控制器
```java
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {
    
    @Autowired
    private ConversationService conversationService;
    
    /**
     * 发送聊天消息
     */
    @PostMapping("/chat")
    public Result<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            ChatResponse response = conversationService.chat(request);
            return Result.success(response);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
```

#### 5. 配置文件更新

**application-dev.yml** - 新增 LLM 配置
```yaml
llm:
  siliconflow:
    api-key: sk-xxxxxxxxxxxxxxxxxxxx
    base-url: https://api.siliconflow.cn
    model: Qwen/Qwen2.5-7B-Instruct
```

**LlmConfig.java** - LLM 配置类
```java
@Configuration
public class LlmConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
```

#### 6. 前端界面（Vue.js）

**ChatBox.vue** - 聊天界面组件
```vue
<template>
  <div class="chat-container">
    <div class="messages">
      <div v-for="msg in messages" :key="msg.id" 
           :class="['message', msg.role]">
        {{ msg.content }}
      </div>
    </div>
    <div class="input-area">
      <input v-model="inputMessage" @keyup.enter="sendMessage" 
             placeholder="输入消息..." />
      <button @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      messages: [],
      inputMessage: ''
    }
  },
  methods: {
    async sendMessage() {
      if (!this.inputMessage.trim()) return;
      
      // 添加用户消息
      this.messages.push({
        id: Date.now(),
        role: 'user',
        content: this.inputMessage
      });
      
      const userMessage = this.inputMessage;
      this.inputMessage = '';
      
      // 调用后端 API
      try {
        const response = await fetch('/api/conversation/chat', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ message: userMessage })
        });
        
        const result = await response.json();
        
        // 添加 AI 响应
        this.messages.push({
          id: Date.now(),
          role: 'assistant',
          content: result.data.message
        });
      } catch (error) {
        console.error('发送消息失败:', error);
      }
    }
  }
}
</script>
```

### 💾 数据库内容
- ❌ 本版本未创建数据库表
- 💡 对话数据暂存于内存，未持久化

### 📝 API 接口

| 方法 | 路径 | 描述 | 请求体 | 响应示例 |
|------|------|------|--------|----------|
| POST | `/api/conversation/chat` | 发送聊天消息 | `{"message": "你好"}` | `{"code":200,"message":"success","data":{"message":"你好！有什么我可以帮助你的吗？","tokens":45}}` |

### ✅ 功能特性
1. ✅ LLM 基础对话能力
2. ✅ SiliconFlow API 集成
3. ✅ DDD 架构分层实现
4. ✅ 统一响应格式封装
5. ✅ 前端聊天界面（Vue.js）
6. ✅ 多服务商扩展能力（抽象基类设计）

### 🎓 技术亮点

#### 1. DDD 架构实践
- **领域层**：定义 LLM 核心模型和服务接口
- **应用层**：编排业务流程，调用领域服务
- **基础设施层**：实现外部 API 集成
- **接口层**：处理 HTTP 请求响应

#### 2. 抽象化设计
通过 `AbstractLlmService` 抽象基类，为后续接入更多 LLM 服务商（OpenAI、Claude、国内大模型等）预留扩展点。

#### 3. 请求响应封装
使用 `Result<T>` 泛型类统一封装 API 响应，规范接口返回格式。

### 📊 代码统计
- **Java 类**: 14 个
- **前端组件**: 11 个 Vue 文件
- **配置文件**: 3 个
- **代码行数**: ~800 行

### 🔄 与 v1.0 的差异
| 维度 | v1.0 | v1.1 |
|------|------|------|
| Java 文件数 | 2 | 14 |
| 功能模块 | 健康检查 | LLM 对话 |
| 架构层次 | 1层（接口层） | 4层（完整 DDD） |
| 外部集成 | 无 | SiliconFlow API |
| 前端界面 | 无 | Vue.js 聊天界面 |

---

## v1.2 - AgentX-2025-03-19-feat-llm-chat-stream

### 📅 版本信息
- **发布日期**: 2025-03-19
- **版本主题**: 实现流式对话响应（SSE）
- **Java 文件数**: 16 (+2)
- **开发周期**: 半天
- **从 v1.1 变更**: 新增流式输出能力

### 🎯 版本目标
实现 Server-Sent Events (SSE) 流式响应，提升用户体验，支持 AI 回复的逐字显示。

### 📦 新增/修改文件

```
AgentX/
├── src/main/java/org/xhy/
│   ├── application/conversation/
│   │   ├── dto/
│   │   │   ├── StreamChatRequest.java        # 🆕 流式请求 DTO
│   │   │   └── StreamChatResponse.java       # 🆕 流式响应 DTO
│   │   └── service/
│   │       └── ConversationService.java      # 🔄 新增流式方法
│   ├── domain/llm/service/
│   │   └── LlmService.java                   # 🔄 新增流式接口
│   ├── infrastructure/integration/llm/
│   │   ├── AbstractLlmService.java           # 🔄 新增流式实现
│   │   └── siliconflow/
│   │       └── SiliconFlowLlmService.java   # 🔄 实现 SSE 处理
│   └── interfaces/api/conversation/
│       └── ConversationController.java       # 🔄 新增流式端点
```

### 🆕 新增内容

#### 1. 流式请求响应 DTO

**StreamChatRequest.java**
```java
public class StreamChatRequest {
    private String message;
    private String sessionId;  // 可选：会话ID
    
    // Getters, Setters
}
```

**StreamChatResponse.java**
```java
public class StreamChatResponse {
    private String content;     // 流式内容片段
    private Boolean finished;   // 是否完成
    private Integer tokens;     // token 使用量（最后一条消息）
    
    // Getters, Setters
}
```

#### 2. LLM 服务接口扩展

**LlmService.java** - 新增流式方法
```java
public interface LlmService {
    /**
     * 普通对话
     */
    LlmResponse chat(LlmRequest request);
    
    /**
     * 流式对话
     */
    Flux<String> chatStream(LlmRequest request);  // 🆕 使用 Reactor Flux
}
```

**SiliconFlowLlmService.java** - 实现流式处理
```java
@Service
public class SiliconFlowLlmService extends AbstractLlmService {
    
    @Override
    public Flux<String> chatStream(LlmRequest request) {
        return Flux.create(sink -> {
            try {
                // 1. 构建 SSE 请求
                request.setStream(true);
                String url = baseUrl + "/v1/chat/completions";
                
                // 2. 使用 OkHttp 处理 SSE
                OkHttpClient client = new OkHttpClient();
                Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(buildRequestBody(request))
                    .headers(Headers.of(buildHeaders()))
                    .build();
                
                // 3. 处理流式响应
                Response response = client.newCall(httpRequest).execute();
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(response.body().byteStream())
                );
                
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6);
                        if ("[DONE]".equals(data)) {
                            sink.complete();
                            break;
                        }
                        
                        // 解析 JSON 并提取 content
                        JSONObject json = JSON.parseObject(data);
                        String content = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("delta")
                            .getString("content");
                        
                        if (content != null) {
                            sink.next(content);
                        }
                    }
                }
            } catch (Exception e) {
                sink.error(e);
            }
        });
    }
}
```

#### 3. 应用服务扩展

**ConversationService.java** - 新增流式方法
```java
@Service
public class ConversationService {
    
    @Autowired
    private LlmService llmService;
    
    /**
     * 流式对话
     */
    public Flux<StreamChatResponse> chatStream(StreamChatRequest request) {
        // 1. 构建 LLM 请求
        LlmRequest llmRequest = LlmRequest.builder()
            .model("Qwen/Qwen2.5-7B-Instruct")
            .messages(Arrays.asList(
                new LlmMessage("user", request.getMessage())
            ))
            .temperature(0.7)
            .stream(true)
            .build();
        
        // 2. 调用流式服务
        return llmService.chatStream(llmRequest)
            .map(content -> {
                StreamChatResponse response = new StreamChatResponse();
                response.setContent(content);
                response.setFinished(false);
                return response;
            })
            .concatWith(Mono.fromSupplier(() -> {
                // 最后一条消息标记完成
                StreamChatResponse finalResponse = new StreamChatResponse();
                finalResponse.setFinished(true);
                return finalResponse;
            }));
    }
}
```

#### 4. 接口层实现

**ConversationController.java** - 新增 SSE 端点
```java
@RestController
@RequestMapping("/api/conversation")
public class ConversationController {
    
    @Autowired
    private ConversationService conversationService;
    
    /**
     * 流式聊天接口
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<StreamChatResponse>> chatStream(
            @RequestBody StreamChatRequest request) {
        
        return conversationService.chatStream(request)
            .map(response -> ServerSentEvent.<StreamChatResponse>builder()
                .data(response)
                .build()
            )
            .onErrorResume(e -> {
                StreamChatResponse errorResponse = new StreamChatResponse();
                errorResponse.setContent("错误: " + e.getMessage());
                errorResponse.setFinished(true);
                return Flux.just(ServerSentEvent.<StreamChatResponse>builder()
                    .data(errorResponse)
                    .build());
            });
    }
}
```

#### 5. 前端更新

**ChatBox.vue** - 支持流式显示
```vue
<script>
export default {
  methods: {
    async sendMessage() {
      if (!this.inputMessage.trim()) return;
      
      // 添加用户消息
      this.messages.push({
        id: Date.now(),
        role: 'user',
        content: this.inputMessage
      });
      
      const userMessage = this.inputMessage;
      this.inputMessage = '';
      
      // 创建 AI 消息占位
      const aiMessage = {
        id: Date.now() + 1,
        role: 'assistant',
        content: ''
      };
      this.messages.push(aiMessage);
      
      // 使用 EventSource 接收流式响应
      const eventSource = new EventSource(
        `/api/conversation/chat/stream?message=${encodeURIComponent(userMessage)}`
      );
      
      eventSource.onmessage = (event) => {
        const response = JSON.parse(event.data);
        
        if (response.finished) {
          eventSource.close();
        } else {
          // 逐字追加内容
          aiMessage.content += response.content;
        }
      };
      
      eventSource.onerror = (error) => {
        console.error('SSE 错误:', error);
        eventSource.close();
      };
    }
  }
}
</script>
```

### 💾 数据库内容
- ❌ 本版本仍未创建数据库表
- 💡 对话数据暂存于内存

### 📝 API 接口

| 方法 | 路径 | 描述 | Content-Type | 响应格式 |
|------|------|------|--------------|----------|
| POST | `/api/conversation/chat` | 普通对话 | `application/json` | JSON |
| POST | `/api/conversation/chat/stream` | 流式对话 | `text/event-stream` | SSE |

**SSE 响应示例**:
```
data: {"content":"你","finished":false}

data: {"content":"好","finished":false}

data: {"content":"！","finished":false}

data: {"content":"","finished":true,"tokens":45}
```

### ✅ 功能特性
1. ✅ Server-Sent Events (SSE) 流式响应
2. ✅ 实时逐字显示 AI 回复
3. ✅ 改善用户体验（降低感知延迟）
4. ✅ 支持长文本生成场景
5. ✅ 错误处理和连接管理

### 🎓 技术亮点

#### 1. Reactive Programming
使用 Project Reactor 的 `Flux` 实现响应式流处理，天然支持背压（Backpressure）。

#### 2. SSE 协议
采用标准 SSE 协议，比 WebSocket 更轻量，单向通信场景下性能更优。

#### 3. 流式解析
实时解析 LLM API 返回的流式数据，边接收边处理边发送。

### 📊 代码统计
- **Java 类**: 16 个
- **新增/修改文件**: 6 个
- **代码行数**: ~950 行

### 🔄 与 v1.1 的差异

| 维度 | v1.1 | v1.2 |
|------|------|------|
| 响应方式 | 一次性返回 | 流式输出 |
| 用户体验 | 等待时间长 | 实时显示 |
| 适用场景 | 短文本 | 短文本 + 长文本 |
| 技术栈 | RestTemplate | RestTemplate + Reactor + SSE |

---


