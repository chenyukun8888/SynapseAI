# AgentX MVC 重构 - 分支架构设计

## 📋 文档说明

本文档为 AgentX 项目从 DDD 架构重构到 MVC 架构提供分支级别的项目结构设计。
**只包含架构设计和项目结构**，不包含功能实现细节。

---

## 🎯 v1.0 - AgentX-2025-03-19-feat-init

### 📂 项目结构

```
agentx-backend/
├── pom.xml                         # 父 POM
│
├── agentx-common/                  # 公共模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/common/
│       ├── base/
│       │   ├── BaseEntity.java
│       │   ├── BaseController.java
│       │   └── BaseService.java
│       ├── result/
│       │   ├── Result.java
│       │   ├── PageResult.java
│       │   └── ResultCode.java
│       ├── exception/
│       │   ├── BusinessException.java
│       │   └── ErrorCode.java
│       ├── utils/
│       │   ├── JsonUtils.java
│       │   └── DateUtils.java
│       └── constants/
│           └── CommonConstants.java
│
├── agentx-infrastructure/          # 基础设施模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/infrastructure/
│       └── database/
│           └── config/
│               └── DataSourceConfig.java
│
└── agentx-app/                     # 应用启动模块
    ├── pom.xml
    ├── src/main/java/com/agentx/
    │   ├── AgentXApplication.java
    │   └── controller/
    │       └── HealthController.java
    └── src/main/resources/
        ├── application.yml
        └── db/migration/
            └── V1.0__init_schema.sql
```

### 📦 Maven 模块

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-app</module>
</modules>
```

---

## 🔹 v1.1 - AgentX-2025-03-19-feat-llm-chat

### 📂 项目结构（新增部分）

```
agentx-backend/
├── agentx-infrastructure/          # 增强
│   └── src/main/java/com/agentx/infrastructure/
│       └── external/
│           └── llm/
│               ├── LlmClientConfig.java
│               ├── SiliconFlowClient.java
│               └── dto/
│                   ├── LlmRequest.java
│                   └── LlmResponse.java
│
├── agentx-module-llm/              # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/llm/
│       ├── controller/
│       │   └── ChatController.java
│       ├── service/
│       │   ├── LlmService.java
│       │   └── impl/
│       │       └── LlmServiceImpl.java
│       ├── mapper/
│       │   └── LlmMessageMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   └── LlmMessageEntity.java
│       │   ├── dto/
│       │   │   ├── ChatRequest.java
│       │   │   └── ChatResponse.java
│       │   └── vo/
│       │       └── MessageVO.java
│       ├── converter/
│       │   └── LlmMessageConverter.java
│       └── enums/
│           └── MessageRole.java
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   └── LlmMessageMapper.xml
        └── db/migration/
            └── V1.1__llm_module.sql
```

### 📦 Maven 模块更新

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-module-llm</module>         <!-- 新增 -->
    <module>agentx-app</module>
</modules>
```

---

## 🔹 v1.2 - AgentX-2025-03-19-feat-llm-chat-stream

### 📂 项目结构（新增/增强部分）

```
agentx-backend/
├── agentx-infrastructure/
│   └── src/main/java/com/agentx/infrastructure/
│       └── external/llm/
│           ├── SiliconFlowClient.java          # 增强流式支持
│           └── SseEmitterManager.java          # 新增
│
└── agentx-module-llm/
    └── src/main/java/com/agentx/module/llm/
        ├── controller/
        │   ├── ChatController.java             # 保持
        │   └── StreamChatController.java       # 新增
        ├── service/
        │   ├── LlmService.java
        │   ├── StreamChatService.java          # 新增
        │   └── impl/
        │       ├── LlmServiceImpl.java
        │       └── StreamChatServiceImpl.java  # 新增
        └── model/dto/
            ├── StreamChatRequest.java          # 新增
            └── StreamChunkVO.java              # 新增
```

---

## 🔄 v2.0 - AgentX-2025-03-20-feat-llm-chat-group

### 📂 项目结构（新增部分）

```
agentx-backend/
├── agentx-module-conversation/     # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/conversation/
│       ├── controller/
│       │   ├── SessionController.java
│       │   ├── MessageController.java
│       │   └── ChatController.java
│       ├── service/
│       │   ├── SessionService.java
│       │   ├── MessageService.java
│       │   ├── ContextService.java
│       │   └── impl/
│       │       ├── SessionServiceImpl.java
│       │       ├── MessageServiceImpl.java
│       │       └── ContextServiceImpl.java
│       ├── mapper/
│       │   ├── SessionMapper.java
│       │   ├── MessageMapper.java
│       │   └── ContextMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   ├── SessionEntity.java
│       │   │   ├── MessageEntity.java
│       │   │   └── ContextEntity.java
│       │   ├── dto/
│       │   │   ├── CreateSessionRequest.java
│       │   │   ├── UpdateSessionRequest.java
│       │   │   └── SessionDTO.java
│       │   └── vo/
│       │       ├── SessionVO.java
│       │       └── MessageVO.java
│       ├── converter/
│       │   ├── SessionConverter.java
│       │   └── MessageConverter.java
│       └── enums/
│           └── SessionStatus.java
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── SessionMapper.xml           # 新增
        │   ├── MessageMapper.xml           # 新增
        │   └── ContextMapper.xml           # 新增
        └── db/migration/
            └── V1.2__conversation_module.sql
```

### 📦 Maven 模块更新

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-module-llm</module>
    <module>agentx-module-conversation</module>  <!-- 新增 -->
    <module>agentx-app</module>
</modules>
```

---

## 🔹 v2.1 - AgentX-2025-03-21-feat-agent

### 📂 项目结构（新增部分）

```
agentx-backend/
├── agentx-module-agent/            # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/agent/
│       ├── controller/
│       │   ├── AgentController.java
│       │   ├── AgentVersionController.java
│       │   ├── AgentWorkspaceController.java
│       │   └── AdminAgentController.java
│       ├── service/
│       │   ├── AgentService.java
│       │   ├── AgentVersionService.java
│       │   ├── AgentWorkspaceService.java
│       │   └── impl/
│       │       ├── AgentServiceImpl.java
│       │       ├── AgentVersionServiceImpl.java
│       │       └── AgentWorkspaceServiceImpl.java
│       ├── mapper/
│       │   ├── AgentMapper.java
│       │   ├── AgentVersionMapper.java
│       │   └── AgentWorkspaceMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   ├── AgentEntity.java
│       │   │   ├── AgentVersionEntity.java
│       │   │   └── AgentWorkspaceEntity.java
│       │   ├── dto/
│       │   │   ├── CreateAgentRequest.java
│       │   │   ├── UpdateAgentRequest.java
│       │   │   └── AgentDTO.java
│       │   ├── vo/
│       │   │   ├── AgentVO.java
│       │   │   └── AgentDetailVO.java
│       │   └── request/
│       │       ├── CreateAgentRequest.java
│       │       └── UpdateAgentRequest.java
│       ├── converter/
│       │   ├── AgentConverter.java
│       │   └── AgentVersionConverter.java
│       └── enums/
│           ├── AgentType.java
│           ├── AgentStatus.java
│           └── PublishStatus.java
│
├── agentx-module-conversation/     # 增强
│   └── src/main/java/com/agentx/module/conversation/
│       ├── service/
│       │   ├── AgentChatService.java       # 新增
│       │   └── impl/
│       │       └── AgentChatServiceImpl.java # 新增
│       └── model/entity/
│           └── SessionEntity.java          # 增加 agent_id 字段
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── AgentMapper.xml             # 新增
        │   ├── AgentVersionMapper.xml      # 新增
        │   └── AgentWorkspaceMapper.xml    # 新增
        └── db/migration/
            └── V1.3__agent_module.sql
```

### 📦 Maven 模块更新

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-module-llm</module>
    <module>agentx-module-conversation</module>
    <module>agentx-module-agent</module>        <!-- 新增 -->
    <module>agentx-app</module>
</modules>
```

---

## 🔹 v2.2 - AgentX-2025-03-21-feat-infrastructure

### 📂 项目结构（新增/增强部分）

```
agentx-backend/
├── agentx-common/                  # 增强
│   └── src/main/java/com/agentx/common/
│       ├── annotation/             # 新增
│       │   ├── RequireLogin.java
│       │   └── RequirePermission.java
│       └── exception/              # 增强
│           ├── GlobalExceptionHandler.java     # 新增
│           ├── AuthenticationException.java    # 新增
│           └── ValidationException.java        # 新增
│
└── agentx-infrastructure/          # 大幅增强
    └── src/main/java/com/agentx/infrastructure/
        ├── security/               # 新增
        │   ├── interceptor/
        │   │   └── UserAuthInterceptor.java
        │   ├── context/
        │   │   └── UserContext.java
        │   └── config/
        │       └── SecurityConfig.java
        ├── database/
        │   ├── config/
        │   │   ├── MyBatisPlusConfig.java     # 新增
        │   │   └── DataSourceConfig.java
        │   └── handler/
        │       ├── JsonTypeHandler.java       # 新增
        │       └── SoftDeleteHandler.java     # 新增
        └── config/
            ├── WebMvcConfig.java              # 增强
            └── CorsConfig.java                # 新增
```

---

## 🔹 v2.3 - AgentX-2025-03-22-feat-agent-message

### 📂 项目结构（增强部分）

```
agentx-backend/
├── agentx-module-conversation/
│   └── src/main/java/com/agentx/module/conversation/
│       ├── service/
│       │   ├── MessageService.java             # 重构
│       │   ├── MessageFormatter.java           # 新增
│       │   └── impl/
│       │       ├── MessageServiceImpl.java     # 重构
│       │       └── MessageFormatterImpl.java   # 新增
│       └── model/
│           ├── entity/
│           │   └── MessageEntity.java          # 增强字段
│           └── vo/
│               └── MessageVO.java              # 增强显示
│
└── agentx-module-agent/
    └── src/main/java/com/agentx/module/agent/
        ├── service/
        │   ├── AgentMessageProcessor.java      # 新增
        │   └── impl/
        │       └── AgentMessageProcessorImpl.java # 新增
        └── model/dto/
            └── AgentMessageContext.java        # 新增
```

---

## ⚙️ v3.0 - AgentX-2025-03-25-feat-llm-token-overflow-strategy

### 📂 项目结构（新增/增强部分）

```
agentx-backend/
├── agentx-module-llm/              # 大增强
│   └── src/main/java/com/agentx/module/llm/
│       ├── service/
│       │   ├── TokenManager.java               # 新增
│       │   ├── TokenOverflowStrategy.java      # 新增接口
│       │   └── impl/
│       │       ├── TokenManagerImpl.java       # 新增
│       │       ├── SlidingWindowStrategy.java  # 新增
│       │       ├── SummaryStrategy.java        # 新增
│       │       └── TruncationStrategy.java     # 新增
│       ├── model/
│       │   ├── dto/
│       │   │   ├── TokenUsageDTO.java          # 新增
│       │   │   └── ContextWindowDTO.java       # 新增
│       │   └── entity/
│       │       └── TokenUsageRecordEntity.java # 新增
│       ├── mapper/
│       │   └── TokenUsageRecordMapper.java     # 新增
│       └── enums/
│           └── OverflowStrategy.java           # 新增
│
├── agentx-module-conversation/     # 增强
│   └── src/main/java/com/agentx/module/conversation/
│       └── service/
│           ├── ContextService.java             # 重构
│           └── impl/
│               └── ContextServiceImpl.java     # 重构，集成 Token 管理
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   └── TokenUsageRecordMapper.xml      # 新增
        └── db/migration/
            └── V1.6__token_management.sql
```

---

## 🔹 v3.1 - AgentX-2025-03-26-feat-llm-service-provider

### 📂 项目结构（新增/增强部分）

```
agentx-backend/
├── agentx-infrastructure/          # 增强
│   └── src/main/java/com/agentx/infrastructure/
│       └── external/llm/
│           ├── LlmProviderFactory.java         # 新增
│           ├── AbstractLLMProvider.java        # 新增
│           ├── providers/                      # 新增目录
│           │   ├── OpenAIProvider.java
│           │   ├── AnthropicProvider.java
│           │   └── SiliconFlowProvider.java
│           └── dto/
│               ├── UnifiedLlmRequest.java      # 新增
│               └── UnifiedLlmResponse.java     # 新增
│
├── agentx-module-llm/              # 大重构
│   └── src/main/java/com/agentx/module/llm/
│       ├── controller/
│       │   ├── ProviderController.java         # 新增
│       │   └── ModelController.java            # 新增
│       ├── service/
│       │   ├── ProviderService.java            # 新增
│       │   ├── ModelService.java               # 新增
│       │   ├── ProviderSelector.java           # 新增
│       │   ├── ProviderHealthChecker.java      # 新增
│       │   └── impl/
│       │       ├── ProviderServiceImpl.java    # 新增
│       │       ├── ModelServiceImpl.java       # 新增
│       │       ├── ProviderSelectorImpl.java   # 新增
│       │       └── ProviderHealthCheckerImpl.java # 新增
│       ├── mapper/
│       │   ├── ProviderMapper.java             # 新增
│       │   ├── ModelMapper.java                # 新增
│       │   └── ProviderHealthCheckMapper.java  # 新增
│       ├── model/
│       │   ├── entity/
│       │   │   ├── ProviderEntity.java         # 新增
│       │   │   ├── ModelEntity.java            # 新增
│       │   │   └── ProviderHealthCheckEntity.java # 新增
│       │   ├── dto/
│       │   │   ├── ProviderConfigDTO.java      # 新增
│       │   │   └── ModelConfigDTO.java         # 新增
│       │   └── vo/
│       │       ├── ProviderVO.java             # 新增
│       │       └── ModelVO.java                # 新增
│       ├── converter/
│       │   ├── ProviderConverter.java          # 新增
│       │   └── ModelConverter.java             # 新增
│       └── enums/
│           ├── ProviderProtocol.java           # 新增
│           └── ProviderStatus.java             # 新增
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── ProviderMapper.xml              # 新增
        │   ├── ModelMapper.xml                 # 新增
        │   └── ProviderHealthCheckMapper.xml   # 新增
        └── db/migration/
            └── V1.7__llm_service_provider.sql
```

---

## 🔹 v3.2 - AgentX-2025-04-01-feat-llm-service-chat

### 📂 项目结构（新增部分）

```
agentx-backend/
├── agentx-infrastructure/
│   └── src/main/java/com/agentx/infrastructure/
│       └── external/llm/
│           ├── circuit-breaker/                # 新增
│           │   └── ProviderCircuitBreaker.java
│           └── metrics/                        # 新增
│               └── LlmMetricsCollector.java
│
├── agentx-module-llm/
│   └── src/main/java/com/agentx/module/llm/
│       ├── service/
│       │   ├── ProviderRetryHandler.java       # 新增
│       │   ├── ProviderFallbackHandler.java    # 新增
│       │   ├── LlmRequestOptimizer.java        # 新增
│       │   └── impl/
│       │       ├── ProviderRetryHandlerImpl.java    # 新增
│       │       ├── ProviderFallbackHandlerImpl.java # 新增
│       │       └── LlmRequestOptimizerImpl.java     # 新增
│       ├── mapper/
│       │   └── LlmRequestLogMapper.java        # 新增
│       ├── model/
│       │   ├── entity/
│       │   │   └── LlmRequestLogEntity.java    # 新增
│       │   └── dto/
│       │       ├── RetryConfig.java            # 新增
│       │       └── FallbackConfig.java         # 新增
│       └── converter/
│           └── LlmRequestLogConverter.java     # 新增
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   └── LlmRequestLogMapper.xml         # 新增
        └── db/migration/
            └── V1.8__llm_service_optimization.sql
```

---

## 🤖 v4.0 - AgentX-2025-04-06-feat-llm-service-agent

### 📂 项目结构（新增部分）

```
agentx-backend/
├── agentx-module-agent/            # 大增强
│   └── src/main/java/com/agentx/module/agent/
│       ├── service/
│       │   ├── AgentExecutionService.java      # 新增
│       │   ├── AgentStrategyService.java       # 新增
│       │   ├── ToolCallParser.java             # 新增
│       │   └── impl/
│       │       ├── AgentExecutionServiceImpl.java  # 新增
│       │       ├── AgentStrategyServiceImpl.java   # 新增
│       │       └── ToolCallParserImpl.java         # 新增
│       ├── model/
│       │   ├── dto/
│       │   │   ├── AgentExecutionContext.java  # 新增
│       │   │   ├── ToolCallRequest.java        # 新增
│       │   │   └── ToolCallResponse.java       # 新增
│       │   └── entity/
│       │       └── AgentToolEntity.java        # 新增
│       ├── mapper/
│       │   └── AgentToolMapper.java            # 新增
│       ├── converter/
│       │   └── ToolCallConverter.java          # 新增
│       └── enums/
│           └── AgentStrategy.java              # 新增
│
├── agentx-module-tool/             # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/tool/
│       ├── controller/
│       │   └── ToolController.java
│       ├── service/
│       │   ├── ToolService.java
│       │   ├── ToolExecutor.java
│       │   ├── ToolExecutorRegistry.java
│       │   └── impl/
│       │       ├── ToolServiceImpl.java
│       │       └── ToolExecutorRegistryImpl.java
│       ├── executor/               # 工具执行器
│       │   ├── WebSearchExecutor.java
│       │   ├── CodeExecutor.java
│       │   └── CalculatorExecutor.java
│       ├── mapper/
│       │   ├── ToolMapper.java
│       │   └── ToolCallLogMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   ├── ToolEntity.java
│       │   │   └── ToolCallLogEntity.java
│       │   ├── dto/
│       │   │   ├── ToolDefinition.java
│       │   │   └── ToolExecutionResult.java
│       │   └── vo/
│       │       └── ToolVO.java
│       ├── converter/
│       │   └── ToolConverter.java
│       └── enums/
│           ├── ToolCategory.java
│           └── ToolStatus.java
│
├── agentx-module-conversation/     # 增强
│   └── src/main/java/com/agentx/module/conversation/
│       └── service/
│           ├── AgentChatService.java           # 重构
│           └── impl/
│               └── AgentChatServiceImpl.java   # 重构，集成工具调用
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── AgentToolMapper.xml             # 新增
        │   ├── ToolMapper.xml                  # 新增
        │   └── ToolCallLogMapper.xml           # 新增
        └── db/migration/
            └── V1.9__agent_tool_integration.sql
```

### 📦 Maven 模块更新

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-module-llm</module>
    <module>agentx-module-conversation</module>
    <module>agentx-module-agent</module>
    <module>agentx-module-tool</module>         <!-- 新增 -->
    <module>agentx-app</module>
</modules>
```

---

## 🔹 v4.1 - AgentX-2025-04-06-feat-llm-service-agent-2

### 📂 项目结构（增强部分）

```
agentx-backend/
└── agentx-module-agent/
    └── src/main/java/com/agentx/module/agent/
        ├── service/
        │   ├── AgentExecutionService.java      # 重构
        │   ├── ExecutionChainManager.java      # 新增
        │   ├── ParallelToolExecutor.java       # 新增
        │   └── impl/
        │       ├── AgentExecutionServiceImpl.java    # 重构
        │       ├── ExecutionChainManagerImpl.java    # 新增
        │       └── ParallelToolExecutorImpl.java     # 新增
        └── model/dto/
            └── ExecutionChain.java             # 新增
```

---

## 🔹 v4.2 - AgentX-2025-04-11-feat-plugin-market-tools

### 📂 项目结构（大增强）

```
agentx-backend/
└── agentx-module-tool/             # 大增强
    └── src/main/java/com/agentx/module/tool/
        ├── controller/
        │   ├── ToolController.java             # 保持
        │   ├── ToolMarketController.java       # 新增
        │   └── ToolCategoryController.java     # 新增
        ├── service/
        │   ├── ToolMarketService.java          # 新增
        │   ├── ToolInstallService.java         # 新增
        │   ├── ToolSearchService.java          # 新增
        │   ├── ToolRecommendService.java       # 新增
        │   └── impl/
        │       ├── ToolMarketServiceImpl.java       # 新增
        │       ├── ToolInstallServiceImpl.java      # 新增
        │       ├── ToolSearchServiceImpl.java       # 新增
        │       └── ToolRecommendServiceImpl.java    # 新增
        ├── mapper/
        │   ├── ToolMarketMapper.java           # 新增
        │   ├── ToolInstallMapper.java          # 新增
        │   ├── ToolUsageMapper.java            # 新增
        │   └── ToolReviewMapper.java           # 新增
        ├── model/
        │   ├── entity/
        │   │   ├── ToolMarketEntity.java       # 新增
        │   │   ├── ToolInstallEntity.java      # 新增
        │   │   ├── ToolUsageStatEntity.java    # 新增
        │   │   └── ToolReviewEntity.java       # 新增
        │   ├── dto/
        │   │   ├── ToolSearchRequest.java      # 新增
        │   │   └── ToolInstallRequest.java     # 新增
        │   └── vo/
        │       ├── ToolMarketVO.java           # 新增
        │       └── ToolDetailVO.java           # 新增
        ├── converter/
        │   ├── ToolMarketConverter.java        # 新增
        │   └── ToolInstallConverter.java       # 新增
        └── enums/
            └── ToolMarketStatus.java           # 新增
```

### 📂 数据库迁移

```
agentx-app/
└── src/main/resources/
    ├── mapper/
    │   ├── ToolMarketMapper.xml                # 新增
    │   ├── ToolInstallMapper.xml               # 新增
    │   ├── ToolUsageMapper.xml                 # 新增
    │   └── ToolReviewMapper.xml                # 新增
    └── db/migration/
        └── V2.0__tool_market.sql
```

---

## 🔹 v4.3 - AgentX-2025-04-18-feat-llm-service-agent-3

### 📂 项目结构（优化）

```
agentx-backend/
└── agentx-module-agent/
    └── src/main/java/com/agentx/module/agent/
        └── service/
            ├── AgentCacheService.java          # 新增
            ├── AgentPerformanceMonitor.java    # 新增
            └── impl/
                ├── AgentCacheServiceImpl.java       # 新增
                └── AgentPerformanceMonitorImpl.java # 新增
```

---

## 🔹 v4.4 - AgentX-2025-04-20-feat-llm-service-agent-4

### 📂 项目结构（持续优化）

```
agentx-backend/
└── agentx-module-agent/
    └── src/main/java/com/agentx/module/agent/
        └── service/
            ├── AgentRecommendService.java      # 新增
            ├── AgentAnalyticsService.java      # 新增
            └── impl/
                ├── AgentRecommendServiceImpl.java   # 新增
                └── AgentAnalyticsServiceImpl.java   # 新增
```

---

## 🔹 v4.5 - AgentX-2025-04-30-feat-user

### 📂 项目结构（新增模块）

```
agentx-backend/
├── agentx-module-user/             # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/user/
│       ├── controller/
│       │   ├── UserController.java
│       │   ├── AuthController.java
│       │   ├── SsoController.java
│       │   └── UserProfileController.java
│       ├── service/
│       │   ├── UserService.java
│       │   ├── AuthService.java
│       │   ├── SsoService.java
│       │   ├── EmailVerificationService.java
│       │   └── impl/
│       │       ├── UserServiceImpl.java
│       │       ├── AuthServiceImpl.java
│       │       ├── SsoServiceImpl.java
│       │       └── EmailVerificationServiceImpl.java
│       ├── mapper/
│       │   ├── UserMapper.java
│       │   ├── UserProfileMapper.java
│       │   ├── AuthSettingMapper.java
│       │   ├── EmailVerificationMapper.java
│       │   └── UserSessionMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   ├── UserEntity.java
│       │   │   ├── UserProfileEntity.java
│       │   │   ├── AuthSettingEntity.java
│       │   │   ├── EmailVerificationEntity.java
│       │   │   └── UserSessionEntity.java
│       │   ├── dto/
│       │   │   ├── RegisterRequest.java
│       │   │   ├── LoginRequest.java
│       │   │   └── UserDTO.java
│       │   ├── vo/
│       │   │   ├── UserVO.java
│       │   │   └── AuthTokenVO.java
│       │   └── request/
│       │       ├── RegisterRequest.java
│       │       └── UpdateProfileRequest.java
│       ├── converter/
│       │   ├── UserConverter.java
│       │   └── UserProfileConverter.java
│       └── enums/
│           └── UserStatus.java
│
├── agentx-infrastructure/          # 增强
│   └── src/main/java/com/agentx/infrastructure/
│       └── security/
│           ├── jwt/                            # 新增
│           │   ├── JwtTokenProvider.java
│           │   ├── JwtAuthFilter.java
│           │   └── JwtProperties.java
│           ├── oauth/                          # 新增
│           │   ├── GitHubOAuthClient.java
│           │   └── OAuthConfig.java
│           └── context/
│               └── UserContext.java            # 重构
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── UserMapper.xml                  # 新增
        │   ├── UserProfileMapper.xml           # 新增
        │   ├── AuthSettingMapper.xml           # 新增
        │   ├── EmailVerificationMapper.xml     # 新增
        │   └── UserSessionMapper.xml           # 新增
        └── db/migration/
            └── V2.1__user_module.sql
```

### 📦 Maven 模块更新

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-module-user</module>         <!-- 新增 -->
    <module>agentx-module-llm</module>
    <module>agentx-module-conversation</module>
    <module>agentx-module-agent</module>
    <module>agentx-module-tool</module>
    <module>agentx-app</module>
</modules>
```

---

## 🔹 v4.6 - AgentX-2025-04-30-feat-user-dev

### 📂 项目结构（增强）

```
agentx-backend/
└── agentx-module-user/
    └── src/main/java/com/agentx/module/user/
        ├── service/
        │   ├── UserPermissionService.java      # 新增
        │   ├── UserPreferenceService.java      # 新增
        │   └── impl/
        │       ├── UserPermissionServiceImpl.java  # 新增
        │       └── UserPreferenceServiceImpl.java  # 新增
        └── model/
            └── entity/
                └── UserPermissionEntity.java   # 新增
```

---

## 🛠️ v5.0 - AgentX-2025-05-10-feat-plguin-tools

### 📂 项目结构（大重构）

```
agentx-backend/
├── agentx-infrastructure/          # 增强
│   └── src/main/java/com/agentx/infrastructure/
│       └── external/
│           └── mcp/                            # 新增
│               ├── McpGatewayClient.java
│               └── McpProtocolHandler.java
│
└── agentx-module-tool/             # 大重构
    └── src/main/java/com/agentx/module/tool/
        ├── controller/
        │   ├── McpToolController.java          # 新增
        │   └── FunctionToolController.java     # 新增
        ├── service/
        │   ├── McpToolService.java             # 新增
        │   ├── FunctionToolService.java        # 新增
        │   ├── ToolPluginLoader.java           # 新增
        │   └── impl/
        │       ├── McpToolServiceImpl.java          # 新增
        │       ├── FunctionToolServiceImpl.java     # 新增
        │       └── ToolPluginLoaderImpl.java        # 新增
        ├── executor/
        │   ├── McpToolExecutor.java            # 新增
        │   └── FunctionCallExecutor.java       # 新增
        ├── mapper/
        │   ├── McpToolMapper.java              # 新增
        │   ├── FunctionToolMapper.java         # 新增
        │   └── ToolDependencyMapper.java       # 新增
        ├── model/
        │   ├── entity/
        │   │   ├── McpToolEntity.java          # 新增
        │   │   ├── FunctionToolEntity.java     # 新增
        │   │   └── ToolDependencyEntity.java   # 新增
        │   └── dto/
        │       ├── McpToolDefinition.java      # 新增
        │       └── FunctionDefinition.java     # 新增
        ├── converter/
        │   ├── McpToolConverter.java           # 新增
        │   └── FunctionToolConverter.java      # 新增
        └── enums/
            ├── ToolProtocol.java               # 新增
            └── ExecutorType.java               # 新增
```

### 📂 数据库迁移

```
agentx-app/
└── src/main/resources/
    ├── mapper/
    │   ├── McpToolMapper.xml                   # 新增
    │   ├── FunctionToolMapper.xml              # 新增
    │   └── ToolDependencyMapper.xml            # 新增
    └── db/migration/
        └── V2.2__plugin_tools.sql
```

---

## 🔹 v5.1 - AgentX-2025-05-21-feat-tool-llm

### 📂 项目结构（增强）

```
agentx-backend/
├── agentx-module-agent/
│   └── src/main/java/com/agentx/module/agent/
│       └── service/
│           ├── AgentExecutionService.java      # 增强
│           ├── FunctionCallingHandler.java     # 新增
│           └── impl/
│               ├── AgentExecutionServiceImpl.java  # 增强
│               └── FunctionCallingHandlerImpl.java # 新增
│
└── agentx-module-llm/
    └── src/main/java/com/agentx/module/llm/
        └── service/
            ├── FunctionCallingService.java     # 新增
            └── impl/
                └── FunctionCallingServiceImpl.java # 新增
```

---

## 🔹 v5.2 - AgentX-2025-05-26-feat-agent-schedule

### 📂 项目结构（新增模块）

```
agentx-backend/
├── agentx-module-task/             # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/task/
│       ├── controller/
│       │   ├── ScheduledTaskController.java
│       │   ├── TaskHistoryController.java
│       │   └── TraceController.java
│       ├── service/
│       │   ├── ScheduledTaskService.java
│       │   ├── TaskExecutorService.java
│       │   ├── TraceService.java
│       │   └── impl/
│       │       ├── ScheduledTaskServiceImpl.java
│       │       ├── TaskExecutorServiceImpl.java
│       │       └── TraceServiceImpl.java
│       ├── scheduler/
│       │   ├── TaskScheduler.java
│       │   └── CronExpressionParser.java
│       ├── mapper/
│       │   ├── ScheduledTaskMapper.java
│       │   ├── TaskHistoryMapper.java
│       │   ├── TraceStepMapper.java
│       │   └── TraceEventMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   ├── ScheduledTaskEntity.java
│       │   │   ├── TaskHistoryEntity.java
│       │   │   ├── TraceStepEntity.java
│       │   │   └── TraceEventEntity.java
│       │   ├── dto/
│       │   │   ├── CreateTaskRequest.java
│       │   │   └── TaskExecutionDTO.java
│       │   └── vo/
│       │       ├── ScheduledTaskVO.java
│       │       └── TaskHistoryVO.java
│       ├── converter/
│       │   ├── ScheduledTaskConverter.java
│       │   └── TaskHistoryConverter.java
│       └── enums/
│           ├── TaskStatus.java
│           └── ExecutionStatus.java
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── ScheduledTaskMapper.xml         # 新增
        │   ├── TaskHistoryMapper.xml           # 新增
        │   ├── TraceStepMapper.xml             # 新增
        │   └── TraceEventMapper.xml            # 新增
        └── db/migration/
            └── V2.3__scheduled_tasks.sql
```

### 📦 Maven 模块更新

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-module-user</module>
    <module>agentx-module-llm</module>
    <module>agentx-module-conversation</module>
    <module>agentx-module-agent</module>
    <module>agentx-module-tool</module>
    <module>agentx-module-task</module>         <!-- 新增 -->
    <module>agentx-app</module>
</modules>
```

---

## 🔹 v5.3 - AgentX-2025-05-29-feat-chat-preview

### 📂 项目结构（增强）

```
agentx-backend/
└── agentx-module-conversation/
    └── src/main/java/com/agentx/module/conversation/
        ├── controller/
        │   └── ChatPreviewController.java      # 新增
        ├── service/
        │   ├── PreviewService.java             # 新增
        │   └── impl/
        │       └── PreviewServiceImpl.java     # 新增
        └── model/
            └── vo/
                └── PreviewVO.java              # 新增
```

---

## 🎨 v6.0 - AgentX-2025-05-31-feat-chat-multimodal

### 📂 项目结构（增强）

```
agentx-backend/
├── agentx-infrastructure/          # 增强
│   └── src/main/java/com/agentx/infrastructure/
│       └── storage/                            # 新增
│           ├── FileStorageService.java
│           ├── S3StorageService.java
│           ├── OssStorageService.java
│           └── impl/
│               ├── FileStorageServiceImpl.java
│               ├── S3StorageServiceImpl.java
│               └── OssStorageServiceImpl.java
│
└── agentx-module-conversation/     # 增强
    └── src/main/java/com/agentx/module/conversation/
        ├── controller/
        │   ├── FileUploadController.java       # 新增
        │   └── MultimodalChatController.java   # 新增
        ├── service/
        │   ├── FileUploadService.java          # 新增
        │   ├── MultimodalService.java          # 新增
        │   └── impl/
        │       ├── FileUploadServiceImpl.java      # 新增
        │       └── MultimodalServiceImpl.java      # 新增
        ├── mapper/
        │   └── FileAttachmentMapper.java       # 新增
        ├── model/
        │   ├── entity/
        │   │   └── FileAttachmentEntity.java   # 新增
        │   └── dto/
        │       └── MultimodalMessageDTO.java   # 新增
        ├── converter/
        │   └── FileAttachmentConverter.java    # 新增
        └── enums/
            └── FileType.java                   # 新增
```

### 📂 数据库迁移

```
agentx-app/
└── src/main/resources/
    ├── mapper/
    │   └── FileAttachmentMapper.xml            # 新增
    └── db/migration/
        └── V2.4__multimodal_support.sql
```

---

## 🔹 v6.1 - AgentX-2025-06-08-feat-model-high-availability

### 📂 项目结构（增强）

```
agentx-backend/
├── agentx-infrastructure/
│   └── src/main/java/com/agentx/infrastructure/
│       └── external/
│           └── gateway/                        # 新增
│               ├── HighAvailabilityClient.java
│               └── GatewayConfig.java
│
└── agentx-module-llm/
    └── src/main/java/com/agentx/module/llm/
        └── service/
            ├── HighAvailabilityService.java    # 新增
            └── impl/
                └── HighAvailabilityServiceImpl.java # 新增
```

---

## 🔹 v6.2 - AgentX-2025-06-10-feat-interrupt

### 📂 项目结构（增强）

```
agentx-backend/
├── agentx-module-conversation/
│   └── src/main/java/com/agentx/module/conversation/
│       ├── controller/
│       │   └── InterruptController.java        # 新增
│       └── service/
│           ├── InterruptService.java           # 新增
│           └── impl/
│               └── InterruptServiceImpl.java   # 新增
│
└── agentx-module-agent/
    └── src/main/java/com/agentx/module/agent/
        └── service/
            ├── AgentInterruptHandler.java      # 新增
            └── impl/
                └── AgentInterruptHandlerImpl.java # 新增
```

---

## 🔹 v6.3 - AgentX-2025-06-14-feat-open-api

### 📂 项目结构（新增）

```
agentx-backend/
├── agentx-module-user/             # 增强
│   └── src/main/java/com/agentx/module/user/
│       ├── controller/
│       │   └── ApiKeyController.java           # 新增
│       ├── service/
│       │   ├── ApiKeyService.java              # 新增
│       │   └── impl/
│       │       └── ApiKeyServiceImpl.java      # 新增
│       ├── mapper/
│       │   └── ApiKeyMapper.java               # 新增
│       ├── model/
│       │   ├── entity/
│       │   │   └── ApiKeyEntity.java           # 新增
│       │   └── vo/
│       │       └── ApiKeyVO.java               # 新增
│       └── converter/
│           └── ApiKeyConverter.java            # 新增
│
└── agentx-app/
    └── src/main/java/com/agentx/
        └── controller/
            └── OpenApiController.java          # 新增
```

### 📂 数据库迁移

```
agentx-app/
└── src/main/resources/
    ├── mapper/
    │   └── ApiKeyMapper.xml                    # 新增
    └── db/migration/
        └── V2.5__open_api.sql
```

---

## 🔹 v6.4 - AgentX-2025-06-27-feat-docker-manager

### 📂 项目结构（新增模块）

```
agentx-backend/
├── agentx-module-container/        # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/container/
│       ├── controller/
│       │   ├── ContainerController.java
│       │   └── ContainerTemplateController.java
│       ├── service/
│       │   ├── ContainerService.java
│       │   ├── ContainerTemplateService.java
│       │   ├── ContainerMonitorService.java
│       │   ├── ContainerCleanupService.java
│       │   └── impl/
│       │       ├── ContainerServiceImpl.java
│       │       ├── ContainerTemplateServiceImpl.java
│       │       ├── ContainerMonitorServiceImpl.java
│       │       └── ContainerCleanupServiceImpl.java
│       ├── client/
│       │   └── DockerClient.java
│       ├── mapper/
│       │   ├── ContainerMapper.java
│       │   └── ContainerTemplateMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   ├── ContainerEntity.java
│       │   │   └── ContainerTemplateEntity.java
│       │   ├── dto/
│       │   │   ├── CreateContainerRequest.java
│       │   │   └── ContainerConfigDTO.java
│       │   └── vo/
│       │       ├── ContainerVO.java
│       │       └── ContainerStatusVO.java
│       ├── converter/
│       │   ├── ContainerConverter.java
│       │   └── ContainerTemplateConverter.java
│       └── enums/
│           └── ContainerStatus.java
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── ContainerMapper.xml             # 新增
        │   └── ContainerTemplateMapper.xml     # 新增
        └── db/migration/
            └── V2.6__container_module.sql
```

### 📦 Maven 模块更新

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-module-user</module>
    <module>agentx-module-llm</module>
    <module>agentx-module-conversation</module>
    <module>agentx-module-agent</module>
    <module>agentx-module-tool</module>
    <module>agentx-module-task</module>
    <module>agentx-module-container</module>    <!-- 新增 -->
    <module>agentx-app</module>
</modules>
```

---

## 🧠 v7.0-v7.5 - 知识图谱与日志追踪系列

### 📂 v7.0 项目结构（新增 RAG 模块）

```
agentx-backend/
├── agentx-module-rag/              # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/rag/
│       ├── controller/
│       │   ├── RagDatasetController.java
│       │   ├── RagDocumentController.java
│       │   ├── RagSearchController.java
│       │   ├── RagChatController.java
│       │   ├── RagPublishController.java
│       │   ├── RagMarketController.java
│       │   └── RagQaController.java
│       ├── service/
│       │   ├── RagDatasetService.java
│       │   ├── RagDocumentService.java
│       │   ├── RagChunkService.java
│       │   ├── RagEmbeddingService.java
│       │   ├── RagSearchService.java
│       │   ├── RagChatService.java
│       │   ├── RagPublishService.java
│       │   └── impl/
│       │       ├── RagDatasetServiceImpl.java
│       │       ├── RagDocumentServiceImpl.java
│       │       ├── RagChunkServiceImpl.java
│       │       ├── RagEmbeddingServiceImpl.java
│       │       ├── RagSearchServiceImpl.java
│       │       ├── RagChatServiceImpl.java
│       │       └── RagPublishServiceImpl.java
│       ├── processor/
│       │   ├── DocumentParser.java
│       │   ├── ChunkStrategy.java
│       │   └── EmbeddingProcessor.java
│       ├── mapper/
│       │   ├── RagDatasetMapper.java
│       │   ├── RagDocumentMapper.java
│       │   ├── RagChunkMapper.java
│       │   └── RagPublishMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   ├── RagDatasetEntity.java
│       │   │   ├── RagDocumentEntity.java
│       │   │   ├── RagChunkEntity.java
│       │   │   ├── RagPublishEntity.java
│       │   │   └── RagQaDatasetEntity.java
│       │   ├── dto/
│       │   │   ├── CreateDatasetRequest.java
│       │   │   ├── UploadDocumentRequest.java
│       │   │   └── SearchRequest.java
│       │   └── vo/
│       │       ├── RagDatasetVO.java
│       │       ├── RagDocumentVO.java
│       │       └── SearchResultVO.java
│       ├── converter/
│       │   ├── RagDatasetConverter.java
│       │   └── RagDocumentConverter.java
│       └── enums/
│           ├── DatasetStatus.java
│           └── DocumentType.java
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── RagDatasetMapper.xml            # 新增
        │   ├── RagDocumentMapper.xml           # 新增
        │   ├── RagChunkMapper.xml              # 新增
        │   └── RagPublishMapper.xml            # 新增
        └── db/migration/
            └── V3.0__rag_module.sql
```

### 📂 v7.2 项目结构（产品化）

```
agentx-backend/
└── agentx-module-agent/
    └── src/main/java/com/agentx/module/agent/
        ├── controller/
        │   └── AgentWidgetController.java      # 新增
        ├── service/
        │   ├── AgentWidgetService.java         # 新增
        │   └── impl/
        │       └── AgentWidgetServiceImpl.java # 新增
        ├── mapper/
        │   └── AgentWidgetMapper.java          # 新增
        ├── model/entity/
        │   └── AgentWidgetEntity.java          # 新增
        └── converter/
            └── AgentWidgetConverter.java       # 新增
```

### 📂 v7.3-v7.5 项目结构（日志追踪增强）

```
agentx-backend/
├── agentx-infrastructure/
│   └── src/main/java/com/agentx/infrastructure/
│       └── logging/                            # 新增
│           ├── StructuredLogger.java
│           ├── LogContext.java
│           └── LogCollector.java
│
└── agentx-module-task/             # 增强（在 v7.3）
    └── src/main/java/com/agentx/module/task/
        └── model/entity/
            ├── TraceStepEntity.java            # v5.2 已有，增强
            └── TraceEventEntity.java           # v5.2 已有，增强
```

### 📦 Maven 模块更新（v7.0+）

```xml
<modules>
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    <module>agentx-module-user</module>
    <module>agentx-module-llm</module>
    <module>agentx-module-conversation</module>
    <module>agentx-module-agent</module>
    <module>agentx-module-tool</module>
    <module>agentx-module-task</module>
    <module>agentx-module-container</module>
    <module>agentx-module-rag</module>          <!-- v7.0 新增 -->
    <module>agentx-app</module>
</modules>
```

---

## 🚀 v8.0-v8.1 - 长期记忆与多 Agent

### 📂 v8.0 项目结构（长期记忆）

```
agentx-backend/
├── agentx-module-agent/            # 增强
│   └── src/main/java/com/agentx/module/agent/
│       ├── controller/
│       │   └── AgentMemoryController.java      # 新增
│       ├── service/
│       │   ├── AgentMemoryService.java         # 新增
│       │   └── impl/
│       │       └── AgentMemoryServiceImpl.java # 新增
│       ├── mapper/
│       │   └── AgentMemoryMapper.java          # 新增
│       ├── model/
│       │   ├── entity/
│       │   │   └── AgentMemoryEntity.java      # 新增
│       │   └── dto/
│       │       └── MemoryDTO.java              # 新增
│       ├── converter/
│       │   └── AgentMemoryConverter.java       # 新增
│       └── enums/
│           └── MemoryType.java                 # 新增
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   └── AgentMemoryMapper.xml           # 新增
        └── db/migration/
            └── V3.1__agent_memory.sql
```

### 📂 v8.1 项目结构（多 Agent 协作）

```
agentx-backend/
└── agentx-module-agent/            # 增强
    └── src/main/java/com/agentx/module/agent/
        ├── controller/
        │   └── MultiAgentController.java       # 新增
        ├── service/
        │   ├── MultiAgentService.java          # 新增
        │   ├── AgentCommunicationService.java  # 新增
        │   ├── AgentCoordinatorService.java    # 新增
        │   └── impl/
        │       ├── MultiAgentServiceImpl.java       # 新增
        │       ├── AgentCommunicationServiceImpl.java # 新增
        │       └── AgentCoordinatorServiceImpl.java   # 新增
        ├── mapper/
        │   ├── AgentCollaborationMapper.java   # 新增
        │   └── AgentMessageMapper.java         # 新增
        ├── model/
        │   ├── entity/
        │   │   ├── AgentCollaborationEntity.java # 新增
        │   │   └── AgentMessageEntity.java     # 新增
        │   └── dto/
        │       ├── MultiAgentRequest.java      # 新增
        │       └── CollaborationDTO.java       # 新增
        ├── converter/
        │   └── MultiAgentConverter.java        # 新增
        └── enums/
            └── CollaborationMode.java          # 新增
```

### 📂 数据库迁移

```
agentx-app/
└── src/main/resources/
    ├── mapper/
    │   ├── AgentCollaborationMapper.xml        # 新增
    │   └── AgentMessageMapper.xml              # 新增
    └── db/migration/
        └── V3.2__multi_agent.sql
```

---

## 💰 补充：计费系统模块（跨版本功能）

### 📂 项目结构

```
agentx-backend/
├── agentx-module-billing/          # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/billing/
│       ├── controller/
│       │   ├── AccountController.java
│       │   ├── ProductController.java
│       │   ├── OrderController.java
│       │   ├── PaymentController.java
│       │   ├── UsageController.java
│       │   └── RuleController.java
│       ├── service/
│       │   ├── AccountService.java
│       │   ├── ProductService.java
│       │   ├── OrderService.java
│       │   ├── PaymentService.java
│       │   ├── BillingService.java
│       │   ├── UsageRecordService.java
│       │   ├── RuleService.java
│       │   └── impl/
│       │       ├── AccountServiceImpl.java
│       │       ├── ProductServiceImpl.java
│       │       ├── OrderServiceImpl.java
│       │       ├── PaymentServiceImpl.java
│       │       ├── BillingServiceImpl.java
│       │       ├── UsageRecordServiceImpl.java
│       │       └── RuleServiceImpl.java
│       ├── strategy/
│       │   ├── BillingStrategy.java
│       │   ├── TokenBillingStrategy.java
│       │   └── PerUnitBillingStrategy.java
│       ├── payment/
│       │   ├── AlipayPayment.java
│       │   └── StripePayment.java
│       ├── mapper/
│       │   ├── AccountMapper.java
│       │   ├── ProductMapper.java
│       │   ├── OrderMapper.java
│       │   ├── UsageRecordMapper.java
│       │   └── RuleMapper.java
│       ├── model/
│       │   ├── entity/
│       │   │   ├── AccountEntity.java
│       │   │   ├── ProductEntity.java
│       │   │   ├── OrderEntity.java
│       │   │   ├── UsageRecordEntity.java
│       │   │   └── RuleEntity.java
│       │   ├── dto/
│       │   │   ├── RechargeRequest.java
│       │   │   ├── CreateOrderRequest.java
│       │   │   └── PaymentCallback.java
│       │   └── vo/
│       │       ├── AccountVO.java
│       │       ├── OrderVO.java
│       │       └── UsageStatVO.java
│       ├── converter/
│       │   ├── AccountConverter.java
│       │   ├── ProductConverter.java
│       │   └── OrderConverter.java
│       └── enums/
│           ├── OrderStatus.java
│           ├── PaymentMethod.java
│           └── BillingType.java
│
└── agentx-app/
    └── src/main/resources/
        ├── mapper/
        │   ├── AccountMapper.xml               # 新增
        │   ├── ProductMapper.xml               # 新增
        │   ├── OrderMapper.xml                 # 新增
        │   ├── UsageRecordMapper.xml           # 新增
        │   └── RuleMapper.xml                  # 新增
        └── db/migration/
            └── V3.3__billing_module.sql
```

---

## 👨‍💼 补充：管理后台模块

### 📂 项目结构

```
agentx-backend/
├── agentx-module-admin/            # 新增模块
│   ├── pom.xml
│   └── src/main/java/com/agentx/module/admin/
│       ├── controller/
│       │   ├── AdminAgentController.java
│       │   ├── AdminToolController.java
│       │   ├── AdminRagController.java
│       │   ├── AdminUserController.java
│       │   ├── AdminOrderController.java
│       │   ├── AdminProductController.java
│       │   ├── AdminRuleController.java
│       │   └── AdminStatisticsController.java
│       ├── service/
│       │   ├── AdminReviewService.java
│       │   ├── AdminUserService.java
│       │   ├── AdminStatisticsService.java
│       │   ├── AdminConfigService.java
│       │   └── impl/
│       │       ├── AdminReviewServiceImpl.java
│       │       ├── AdminUserServiceImpl.java
│       │       ├── AdminStatisticsServiceImpl.java
│       │       └── AdminConfigServiceImpl.java
│       ├── model/
│       │   ├── dto/
│       │   │   ├── ReviewRequest.java
│       │   │   └── StatisticsQuery.java
│       │   └── vo/
│       │       ├── ReviewVO.java
│       │       └── StatisticsVO.java
│       └── converter/
│           └── AdminConverter.java
│
└── agentx-app/
    └── src/main/resources/
        └── db/migration/
            └── V3.4__admin_module.sql
```

---

## 📊 最终完整模块清单

### 📦 Maven 模块列表（完整版）

```xml
<modules>
    <!-- 基础模块 -->
    <module>agentx-common</module>
    <module>agentx-infrastructure</module>
    
    <!-- 功能模块 -->
    <module>agentx-module-user</module>
    <module>agentx-module-llm</module>
    <module>agentx-module-conversation</module>
    <module>agentx-module-agent</module>
    <module>agentx-module-tool</module>
    <module>agentx-module-task</module>
    <module>agentx-module-container</module>
    <module>agentx-module-rag</module>
    <module>agentx-module-billing</module>
    <module>agentx-module-admin</module>
    
    <!-- 应用启动 -->
    <module>agentx-app</module>
</modules>
```

---

## 📝 重构实施建议

### 1️⃣ 分支策略

```bash
# 主分支
main/master                 # 当前 DDD 版本
refactor/mvc               # MVC 重构主分支

# 模块重构分支
refactor/mvc-common
refactor/mvc-infrastructure
refactor/mvc-user
refactor/mvc-llm
refactor/mvc-conversation
refactor/mvc-agent
refactor/mvc-tool
refactor/mvc-task
refactor/mvc-container
refactor/mvc-rag
refactor/mvc-billing
refactor/mvc-admin
```

### 2️⃣ 重构顺序

```plain
第一步：基础模块
  ✅ agentx-common
  ✅ agentx-infrastructure

第二步：核心模块（按依赖顺序）
  ✅ agentx-module-user      # 用户认证基础
  ✅ agentx-module-llm       # LLM 服务基础
  ✅ agentx-module-conversation  # 对话功能
  ✅ agentx-module-agent     # Agent 核心

第三步：扩展模块
  ✅ agentx-module-tool      # 工具系统
  ✅ agentx-module-rag       # 知识库
  ✅ agentx-module-billing   # 计费系统

第四步：高级模块
  ✅ agentx-module-task      # 定时任务
  ✅ agentx-module-container # 容器管理
  ✅ agentx-module-admin     # 管理后台
```

### 3️⃣ 文件迁移对照表

| DDD 架构 | MVC 架构 |
|---------|---------|
| `domain/model/entity` → | `model/entity` |
| `domain/model/valueobject` → | `model/dto` |
| `domain/service` → | `service` |
| `application/service` → | `service` (合并到一层) |
| `interfaces/api` → | `controller` |
| `infrastructure/persistence` → | `mapper` + `model/entity` |
| `infrastructure/integration` → | `infrastructure/external` |

---

**文档生成时间**: 2025-11-06  
**适用版本**: v1.0 - v8.1+  
**架构模式**: MVC (Model-View-Controller)  
**重构目标**: 从 DDD 架构迁移到 MVC 架构

