# AgentX MVC架构重构设计
## 📐 新项目架构设计（MVC）
### 🎯 核心设计原则
1. **按功能模块垂直划分**，每个模块包含完整的MVC三层
2. **模块独立性**，降低耦合，便于分支开发
3. **统一的基础设施层**，避免重复代码
4. **清晰的依赖关系**，模块间通过接口通信

---

## 📂 项目整体结构
```plain
agentx-backend/
├── agentx-common/              # 公共模块（所有模块依赖）
│   ├── base/                   # 基础类
│   ├── utils/                  # 工具类
│   ├── exception/              # 统一异常处理
│   ├── config/                 # 公共配置
│   └── constants/              # 公共常量
│
├── agentx-infrastructure/      # 基础设施模块（独立）
│   ├── database/               # 数据库配置
│   ├── cache/                  # 缓存配置
│   ├── mq/                     # 消息队列
│   ├── storage/                # 文件存储
│   ├── security/               # 安全认证
│   └── external/               # 外部服务集成
│
├── agentx-module-user/         # 模块1: 用户认证与管理
├── agentx-module-agent/        # 模块2: Agent管理
├── agentx-module-conversation/ # 模块3: 对话系统
├── agentx-module-rag/          # 模块4: RAG知识库
├── agentx-module-tool/         # 模块5: 工具管理
├── agentx-module-llm/          # 模块6: LLM模型管理
├── agentx-module-billing/      # 模块7: 计费系统
├── agentx-module-task/         # 模块8: 定时任务
├── agentx-module-container/    # 模块9: 容器管理
├── agentx-module-admin/        # 模块10: 管理后台
│
└── agentx-app/                 # 应用启动模块
    ├── resources/
    │   ├── application.yml
    │   └── db/migration/       # 数据库迁移脚本（按模块分目录）
    └── AgentXApplication.java
```

---

## 📦 各功能模块详细设计
### 🔹 标准模块结构模板
每个功能模块采用统一的MVC结构：

```plain
agentx-module-{name}/
├── pom.xml
├── src/main/java/com/agentx/module/{name}/
│   ├── controller/             # 控制层
│   │   ├── {Name}Controller.java
│   │   └── Admin{Name}Controller.java  # 管理端（如需要）
│   │
│   ├── service/                # 业务逻辑层
│   │   ├── {Name}Service.java
│   │   └── impl/
│   │       └── {Name}ServiceImpl.java
│   │
│   ├── mapper/                 # 数据访问层
│   │   └── {Name}Mapper.java
│   │
│   ├── model/                  # 数据模型
│   │   ├── entity/             # 实体类（对应数据库表）
│   │   │   └── {Name}Entity.java
│   │   ├── dto/                # 数据传输对象
│   │   │   ├── {Name}DTO.java
│   │   │   └── {Name}QueryDTO.java
│   │   ├── vo/                 # 视图对象（返回给前端）
│   │   │   └── {Name}VO.java
│   │   └── request/            # 请求对象
│   │       ├── Create{Name}Request.java
│   │       └── Update{Name}Request.java
│   │
│   ├── converter/              # 对象转换器
│   │   └── {Name}Converter.java
│   │
│   ├── enums/                  # 枚举类
│   │   └── {Name}Status.java
│   │
│   └── config/                 # 模块配置
│       └── {Name}Config.java
│
└── src/main/resources/
    └── mapper/                 # MyBatis XML
        └── {Name}Mapper.xml
```

---

## 🎨 各功能模块划分
### **模块1: agentx-module-user** 👤
**用户认证与管理**

```plain
功能点：
├── 用户注册/登录/登出
├── 邮箱验证码
├── 密码找回
├── SSO登录（GitHub OAuth等）
├── 个人信息管理
├── 用户设置
└── 认证配置管理

核心类：
├── controller/
│   ├── UserController.java           # 用户基础操作
│   ├── AuthController.java           # 认证相关
│   ├── SsoController.java            # SSO登录
│   └── AuthConfigController.java     # 认证配置
├── service/
│   ├── UserService.java
│   ├── AuthService.java
│   ├── SsoService.java
│   └── EmailVerificationService.java
├── mapper/
│   ├── UserMapper.java
│   └── AuthSettingMapper.java
└── model/entity/
    ├── UserEntity.java
    ├── UserProfileEntity.java
    └── AuthSettingEntity.java

数据库表：
├── users
├── user_profiles
├── auth_settings
└── email_verifications
```

---

### **模块2: agentx-module-agent** 🤖
**Agent管理核心**

```plain
功能点：
├── Agent CRUD
├── Agent版本管理
├── Agent发布/审核
├── Agent工作区
├── Agent Widget（嵌入组件）
├── Agent统计信息
└── 系统提示词生成

核心类：
├── controller/
│   ├── AgentController.java          # Agent基础CRUD
│   ├── AgentVersionController.java   # 版本管理
│   ├── AgentWorkspaceController.java # 工作区
│   ├── AgentWidgetController.java    # Widget
│   └── AgentMarketController.java    # Agent市场
├── service/
│   ├── AgentService.java
│   ├── AgentVersionService.java
│   ├── AgentWorkspaceService.java
│   ├── AgentWidgetService.java
│   └── SystemPromptService.java
├── mapper/
│   ├── AgentMapper.java
│   ├── AgentVersionMapper.java
│   ├── AgentWorkspaceMapper.java
│   └── AgentWidgetMapper.java
└── model/entity/
    ├── AgentEntity.java
    ├── AgentVersionEntity.java
    ├── AgentWorkspaceEntity.java
    └── AgentWidgetEntity.java

数据库表：
├── agents
├── agent_versions
├── agent_workspaces
└── agent_widgets
```

---

### **模块3: agentx-module-conversation** 💬
**对话系统**

```plain
功能点：
├── 会话管理（创建/删除/列表）
├── 消息发送/接收
├── 流式对话（SSE）
├── 上下文管理
├── 对话历史
├── 打断机制
└── 外部API对话

核心类：
├── controller/
│   ├── SessionController.java        # 会话管理
│   ├── ChatController.java           # 对话
│   ├── MessageController.java        # 消息
│   ├── ExternalChatController.java   # 外部API
│   └── WidgetChatController.java     # Widget对话
├── service/
│   ├── SessionService.java
│   ├── ChatService.java
│   ├── MessageService.java
│   ├── ContextService.java
│   └── StreamChatService.java
├── handler/
│   ├── MessageHandler.java           # 消息处理器
│   ├── StreamHandler.java            # 流式处理器
│   └── ToolCallHandler.java          # 工具调用处理器
├── mapper/
│   ├── SessionMapper.java
│   ├── MessageMapper.java
│   └── ContextMapper.java
└── model/entity/
    ├── SessionEntity.java
    ├── MessageEntity.java
    └── ContextEntity.java

数据库表：
├── sessions
├── messages
└── contexts
```

---

### **模块4: agentx-module-rag** 📚
**RAG知识库系统**

```plain
功能点：
├── 数据集管理
├── 文档上传/解析
├── 文档分块
├── 向量嵌入
├── 语义搜索
├── RAG对话
├── 知识库发布/市场
└── QA数据集

核心类：
├── controller/
│   ├── RagDatasetController.java     # 数据集管理
│   ├── RagDocumentController.java    # 文档管理
│   ├── RagSearchController.java      # 搜索
│   ├── RagChatController.java        # RAG对话
│   ├── RagPublishController.java     # 发布
│   ├── RagMarketController.java      # 市场
│   └── RagQaController.java          # QA数据集
├── service/
│   ├── RagDatasetService.java
│   ├── RagDocumentService.java
│   ├── RagChunkService.java
│   ├── RagEmbeddingService.java
│   ├── RagSearchService.java
│   ├── RagChatService.java
│   └── RagPublishService.java
├── processor/
│   ├── DocumentParser.java           # 文档解析
│   ├── ChunkStrategy.java            # 分块策略
│   └── EmbeddingProcessor.java       # 向量化
├── mapper/
│   ├── RagDatasetMapper.java
│   ├── RagDocumentMapper.java
│   ├── RagChunkMapper.java
│   └── RagPublishMapper.java
└── model/entity/
    ├── RagDatasetEntity.java
    ├── RagDocumentEntity.java
    ├── RagChunkEntity.java
    ├── RagPublishEntity.java
    └── RagQaDatasetEntity.java

数据库表：
├── rag_datasets
├── rag_documents
├── rag_chunks (带向量字段)
├── rag_publishes
└── rag_qa_datasets
```

---

### **模块5: agentx-module-tool** 🔧
**工具管理**

```plain
功能点：
├── MCP工具管理
├── Function Calling工具
├── 工具市场
├── 工具提交/审核
└── 自定义工具

核心类：
├── controller/
│   ├── ToolController.java           # 工具管理
│   ├── McpToolController.java        # MCP工具
│   ├── FunctionToolController.java   # Function工具
│   └── ToolMarketController.java     # 工具市场
├── service/
│   ├── ToolService.java
│   ├── McpToolService.java
│   ├── FunctionToolService.java
│   └── ToolMarketService.java
├── mapper/
│   ├── McpToolMapper.java
│   ├── FunctionToolMapper.java
│   └── ToolMarketMapper.java
└── model/entity/
    ├── McpToolEntity.java
    ├── FunctionToolEntity.java
    └── ToolSubmitEntity.java

数据库表：
├── mcp_tools
├── function_tools
└── tool_submits
```

---

### **模块6: agentx-module-llm** 🧠
**LLM模型管理**

```plain
功能点：
├── 模型管理（增删改查）
├── 服务商管理
├── 模型配置
├── 高可用配置
└── 模型调用

核心类：
├── controller/
│   ├── ModelController.java          # 模型管理
│   ├── ProviderController.java       # 服务商管理
│   └── HighAvailabilityController.java # 高可用
├── service/
│   ├── ModelService.java
│   ├── ProviderService.java
│   ├── LlmRequestService.java
│   └── HighAvailabilityService.java
├── client/
│   ├── OpenAiClient.java
│   ├── AnthropicClient.java
│   └── SiliconFlowClient.java
├── mapper/
│   ├── ModelMapper.java
│   └── ProviderMapper.java
└── model/entity/
    ├── ModelEntity.java
    └── ProviderEntity.java

数据库表：
├── models
└── providers
```

---

### **模块7: agentx-module-billing** 💰
**计费系统（含账户、订单、支付）**

```plain
功能点：
├── 用户账户管理
├── 余额充值/扣费
├── 商品管理
├── 计费规则
├── 订单管理
├── 支付集成（支付宝/Stripe）
├── 用量记录
└── API密钥管理

核心类：
├── controller/
│   ├── AccountController.java        # 账户管理
│   ├── ProductController.java        # 商品管理
│   ├── OrderController.java          # 订单管理
│   ├── PaymentController.java        # 支付
│   ├── UsageController.java          # 用量记录
│   ├── ApiKeyController.java         # API密钥
│   └── RuleController.java           # 计费规则
├── service/
│   ├── AccountService.java
│   ├── ProductService.java
│   ├── OrderService.java
│   ├── PaymentService.java
│   ├── BillingService.java
│   ├── UsageRecordService.java
│   ├── ApiKeyService.java
│   └── RuleService.java
├── strategy/
│   ├── BillingStrategy.java          # 计费策略接口
│   ├── TokenBillingStrategy.java     # Token计费
│   └── PerUnitBillingStrategy.java   # 按次计费
├── payment/
│   ├── AlipayPayment.java
│   └── StripePayment.java
├── mapper/
│   ├── AccountMapper.java
│   ├── ProductMapper.java
│   ├── OrderMapper.java
│   ├── UsageRecordMapper.java
│   ├── ApiKeyMapper.java
│   └── RuleMapper.java
└── model/entity/
    ├── AccountEntity.java
    ├── ProductEntity.java
    ├── OrderEntity.java
    ├── UsageRecordEntity.java
    ├── ApiKeyEntity.java
    └── RuleEntity.java

数据库表：
├── accounts
├── products
├── rules
├── orders
├── usage_records
└── api_keys
```

---

### **模块8: agentx-module-task** ⏰
**定时任务与执行追踪**

```plain
功能点：
├── 定时任务管理
├── Cron表达式配置
├── 任务执行历史
├── 任务调度
└── Agent执行追踪

核心类：
├── controller/
│   ├── ScheduledTaskController.java  # 定时任务
│   └── TraceController.java          # 执行追踪
├── service/
│   ├── ScheduledTaskService.java
│   ├── TaskExecutorService.java
│   └── TraceService.java
├── scheduler/
│   └── TaskScheduler.java            # 任务调度器
├── mapper/
│   ├── ScheduledTaskMapper.java
│   ├── TaskHistoryMapper.java
│   └── TraceMapper.java
└── model/entity/
    ├── ScheduledTaskEntity.java
    ├── TaskHistoryEntity.java
    ├── TraceStepEntity.java
    └── TraceEventEntity.java

数据库表：
├── scheduled_tasks
├── task_histories
├── trace_steps
└── trace_events
```

---

### **模块9: agentx-module-container** 🐳
**容器管理**

```plain
功能点：
├── 容器创建/删除
├── 容器模板管理
├── 容器监控
├── 容器清理
└── 容器审核

核心类：
├── controller/
│   ├── ContainerController.java      # 容器管理
│   └── ContainerTemplateController.java # 模板管理
├── service/
│   ├── ContainerService.java
│   ├── ContainerTemplateService.java
│   ├── ContainerMonitorService.java
│   └── ContainerCleanupService.java
├── client/
│   └── DockerClient.java             # Docker客户端
├── mapper/
│   ├── ContainerMapper.java
│   └── ContainerTemplateMapper.java
└── model/entity/
    ├── ContainerEntity.java
    └── ContainerTemplateEntity.java

数据库表：
├── containers
└── container_templates
```

---

### **模块10: agentx-module-admin** 👨‍💼
**管理后台（聚合所有管理功能）**

```plain
功能点：
├── Agent审核
├── 工具审核
├── RAG审核
├── 用户管理
├── 订单管理
├── 系统配置
└── 数据统计

核心类：
├── controller/
│   ├── AdminAgentController.java
│   ├── AdminToolController.java
│   ├── AdminRagController.java
│   ├── AdminUserController.java
│   ├── AdminOrderController.java
│   ├── AdminProductController.java
│   ├── AdminRuleController.java
│   └── AdminStatisticsController.java
├── service/
│   ├── AdminReviewService.java
│   ├── AdminUserService.java
│   ├── AdminStatisticsService.java
│   └── AdminConfigService.java
└── (依赖其他模块的Mapper和Service)
```

---

## 🔧 基础模块设计
### **agentx-common** 公共模块
```plain
src/main/java/com/agentx/common/
├── base/
│   ├── BaseEntity.java               # 基础实体（id, created_at等）
│   ├── BaseController.java           # 基础控制器
│   └── BaseService.java              # 基础服务
├── result/
│   ├── Result.java                   # 统一返回结果
│   ├── PageResult.java               # 分页结果
│   └── ResultCode.java               # 返回码
├── exception/
│   ├── BusinessException.java        # 业务异常
│   ├── GlobalExceptionHandler.java   # 全局异常处理
│   └── ErrorCode.java                # 错误码
├── utils/
│   ├── BeanUtils.java
│   ├── JsonUtils.java
│   ├── DateUtils.java
│   ├── IdGenerator.java
│   └── EncryptUtils.java
├── constants/
│   ├── CommonConstants.java
│   └── RegexConstants.java
└── annotation/
    ├── RequireLogin.java             # 需要登录注解
    └── RequirePermission.java        # 需要权限注解
```

---

### **agentx-infrastructure** 基础设施模块
```plain
src/main/java/com/agentx/infrastructure/
├── database/
│   ├── config/
│   │   ├── MyBatisPlusConfig.java
│   │   └── DataSourceConfig.java
│   └── handler/
│       └── JsonTypeHandler.java      # JSON字段处理器
├── security/
│   ├── jwt/
│   │   ├── JwtTokenProvider.java
│   │   └── JwtAuthFilter.java
│   ├── context/
│   │   └── UserContext.java          # 用户上下文
│   └── config/
│       └── SecurityConfig.java
├── storage/
│   ├── FileStorageService.java
│   ├── S3StorageService.java
│   └── OssStorageService.java
├── cache/
│   └── RedisService.java
├── mq/
│   ├── RabbitMqConfig.java
│   └── MessageProducer.java
└── external/
    ├── gateway/
    │   ├── McpGatewayClient.java     # MCP网关客户端
    │   └── HighAvailabilityClient.java
    └── langchain4j/
        └── LangChain4jConfig.java
```

---

## 📊 模块依赖关系
```plain
agentx-app
    ├─→ agentx-module-user
    ├─→ agentx-module-agent
    ├─→ agentx-module-conversation
    ├─→ agentx-module-rag
    ├─→ agentx-module-tool
    ├─→ agentx-module-llm
    ├─→ agentx-module-billing
    ├─→ agentx-module-task
    ├─→ agentx-module-container
    └─→ agentx-module-admin
         └─→ (其他所有模块)

所有模块 ─→ agentx-infrastructure ─→ agentx-common
```

---

## 🗂️ 数据库迁移脚本组织
```plain
agentx-app/src/main/resources/db/migration/
├── V1.0__user_module.sql              # 用户模块表
├── V1.1__agent_module.sql             # Agent模块表
├── V1.2__conversation_module.sql      # 对话模块表
├── V1.3__rag_module.sql               # RAG模块表
├── V1.4__tool_module.sql              # 工具模块表
├── V1.5__llm_module.sql               # LLM模块表
├── V1.6__billing_module.sql           # 计费模块表
├── V1.7__task_module.sql              # 任务模块表
├── V1.8__container_module.sql         # 容器模块表
├── V2.0__init_data.sql                # 初始化数据
└── V3.0__module_xxx_enhancement.sql   # 后续优化
```

---

## 🌳 分支开发策略
```bash
# 主分支
master                   # 生产稳定版本

# 开发主分支
develop                  # 开发集成分支

# 功能分支（每个模块独立分支）
feature/module-user          # 用户模块
feature/module-agent         # Agent模块
feature/module-conversation  # 对话模块
feature/module-rag           # RAG模块
feature/module-tool          # 工具模块
feature/module-llm           # LLM模块
feature/module-billing       # 计费模块
feature/module-task          # 任务模块
feature/module-container     # 容器模块
feature/module-admin         # 管理模块
feature/infrastructure       # 基础设施
feature/common               # 公共模块
```

---

## 🚀 实施步骤建议
### **第一阶段：基础搭建**
1. 创建 `agentx-common` 和 `agentx-infrastructure`
2. 搭建 `agentx-app` 启动模块
3. 配置数据库、Redis、RabbitMQ

### **第二阶段：核心模块（按优先级）**
1. ✅ `agentx-module-user` （认证基础）
2. ✅ `agentx-module-llm` （模型基础）
3. ✅ `agentx-module-agent` （核心业务）
4. ✅ `agentx-module-conversation` （对话功能）

### **第三阶段：扩展模块**
5. ✅ `agentx-module-tool` （工具能力）
6. ✅ `agentx-module-rag` （知识增强）
7. ✅ `agentx-module-billing` （商业化）

### **第四阶段：高级功能**
8. ✅ `agentx-module-task` （任务调度）
9. ✅ `agentx-module-container` （容器编排）
10. ✅ `agentx-module-admin` （管理后台）

---

## 📝 配置文件示例
### **根pom.xml**
```xml
<modules>
    <module>agentx-common</module>

    <module>agentx-infrastructure</module>

    <module>agentx-module-user</module>

    <module>agentx-module-agent</module>

    <module>agentx-module-conversation</module>

    <module>agentx-module-rag</module>

    <module>agentx-module-tool</module>

    <module>agentx-module-llm</module>

    <module>agentx-module-billing</module>

    <module>agentx-module-task</module>

    <module>agentx-module-container</module>

    <module>agentx-module-admin</module>

    <module>agentx-app</module>

</modules>

```

