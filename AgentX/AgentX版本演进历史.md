# AgentX 项目版本演进历史

## 项目概述

AgentX 是一个基于大型语言模型（LLM）和多能力平台（MCP）的智能 Agent 构建平台，采用 DDD（领域驱动设计）架构。项目从 2025年3月19日 开始，历经 32 个版本迭代，从基础的项目初始化逐步发展成为功能完整的 Agent 平台。

## 架构说明

### DDD 架构分层
- **接口层(Interfaces)**: 负责与外部系统交互，包括API接口、前端界面等
- **应用层(Application)**: 负责业务流程编排，调用领域服务完成业务逻辑
- **领域层(Domain)**: 包含核心业务逻辑和领域模型
- **基础设施层(Infrastructure)**: 提供技术支持，如数据持久化、外部服务等

### 技术栈演进
- **初期**: Java 1.8, Spring Boot 2.7.x
- **后期**: Java 17+, Spring Boot 3.x
- **数据库**: PostgreSQL 14.x + pgvector
- **前端**: Vue.js → Next.js + TypeScript
- **容器化**: Docker & Docker Compose

---

## 版本演进时间线

### 🎯 第一阶段：基础对话功能 (2025-03-19)

#### v1.0 - AgentX-2025-03-19-feat-init
**发布日期**: 2025-03-19  
**版本主题**: 项目初始化

**主要内容**:
- ✅ 创建基础项目结构（DDD架构）
- ✅ 搭建开发环境
- ✅ 配置 Docker 和 PostgreSQL
- ✅ 实现健康检查接口

**代码结构**:
```
org.xhy
  └── interfaces
      └── api
          └── base
              └── HealthController
```

---

#### v1.1 - AgentX-2025-03-19-feat-llm-chat
**发布日期**: 2025-03-19  
**版本主题**: 实现基础 LLM 对话功能

**主要内容**:
- ✅ 实现与 LLM 的基础对话
- ✅ 接入 SiliconFlow LLM 服务
- ✅ 设计领域模型（LlmMessage, LlmRequest, LlmResponse）
- ✅ 实现对话 API 接口

**新增模块**:
- `domain/llm`: LLM 领域模型和服务
- `application/conversation`: 对话应用服务
- `infrastructure/integration/llm`: LLM 服务集成

**技术亮点**:
- 抽象化 LLM 服务，便于后续扩展多个服务商
- 采用 AbstractLlmService 作为基类

---

#### v1.2 - AgentX-2025-03-19-feat-llm-chat-stream
**发布日期**: 2025-03-19  
**版本主题**: 实现流式对话响应

**主要内容**:
- ✅ 实现 SSE（Server-Sent Events）流式响应
- ✅ 支持实时对话流输出
- ✅ 优化用户体验，逐字显示 AI 回复

**新增功能**:
- StreamChatRequest/StreamChatResponse DTO
- 流式对话控制器

---

### 🔄 第二阶段：会话管理与 Agent 功能 (2025-03-20 ~ 2025-03-22)

#### v2.0 - AgentX-2025-03-20-feat-llm-chat-group
**发布日期**: 2025-03-20  
**版本主题**: 会话分组与上下文管理

**主要内容**:
- ✅ 实现消息分组功能（按话题/会话分组）
- ✅ 实现上下文管理（维护对话历史）
- ✅ 支持多会话管理
- ✅ 完善前端聊天界面

**新增领域模型**:
- `Session`: 会话实体
- `Message`: 消息实体
- `Context`: 上下文管理

**数据库设计**:
- sessions 表：会话信息
- messages 表：消息记录
- contexts 表：上下文数据

**前端升级**:
- 引入 agentx-frontend-plus（Next.js + TypeScript）
- 会话历史浏览功能

---

#### v2.1 - AgentX-2025-03-21-feat-agent
**发布日期**: 2025-03-21  
**版本主题**: Agent 管理功能

**主要内容**:
- ✅ 实现 Agent 数据模型
- ✅ Agent 创建、修改、删除、查询 API
- ✅ Agent 与会话关联
- ✅ Agent 配置管理（性格、知识领域等）
- ✅ Agent 版本管理与发布

**新增领域**:
```
domain/agent
  ├── model
  │   ├── AgentEntity
  │   ├── AgentVersionEntity
  │   ├── AgentType (枚举)
  │   ├── AgentStatus (枚举)
  │   └── PublishStatus (枚举)
  └── service
      └── AgentService
```

**API 设计**:
- `/api/admin/agents`: 管理员 Agent 管理
- `/api/portal/agents`: 用户 Agent 使用

**关键特性**:
- Agent 工具集成
- 模型配置管理
- 版本发布与审核机制

---

#### v2.2 - AgentX-2025-03-21-feat-infrastructure
**发布日期**: 2025-03-21  
**版本主题**: 基础设施完善

**主要内容**:
- ✅ 用户认证拦截器
- ✅ 全局异常处理
- ✅ 用户上下文管理
- ✅ WebMvc 配置优化
- ✅ 数据库 Schema 初始化脚本

**基础设施增强**:
- `UserAuthInterceptor`: 用户认证
- `GlobalExceptionHandler`: 统一异常处理
- `UserContext`: 线程上下文管理

---

#### v2.3 - AgentX-2025-03-22-feat-agent-message
**发布日期**: 2025-03-22  
**版本主题**: Agent 消息优化

**主要内容**:
- ✅ 优化 Agent 消息处理流程
- ✅ 改进消息存储机制
- ✅ 完善前端消息展示

---

### ⚙️ 第三阶段：LLM 服务抽象与优化 (2025-03-25 ~ 2025-04-01)

#### v3.0 - AgentX-2025-03-25-feat-llm-token-overflow-strategy
**发布日期**: 2025-03-25  
**版本主题**: Token 溢出策略

**主要内容**:
- ✅ 实现 Token 上下文管理
- ✅ 滑动窗口策略
- ✅ 摘要算法处理长上下文
- ✅ 优化 LLM 调用成本

**技术方案**:
- 基于 Token 数量的滑动窗口
- 自动摘要历史对话
- 保持关键上下文信息

**开发规范更新**:
- 新增测试规范（SpringBoot Test）
- 新增 SQL 管理规范（文档与代码分离）

---

#### v3.1 - AgentX-2025-03-26-feat-llm-service-provider
**发布日期**: 2025-03-26  
**版本主题**: LLM 服务商抽象

**主要内容**:
- ✅ 设计服务商接口规范
- ✅ 实现服务商基类
- ✅ 服务商配置管理
- ✅ 支持多服务商接入

**架构升级**:
- 服务商插件化架构
- 统一的服务商接口
- 灵活的配置管理

**为后续扩展做准备**:
- OpenAI
- Anthropic (Claude)
- 国内大模型（百度文心、阿里通义、讯飞星火等）

---

#### v3.2 - AgentX-2025-04-01-feat-llm-service-chat
**发布日期**: 2025-04-01  
**版本主题**: LLM 服务对话优化

**主要内容**:
- ✅ 优化 LLM 对话服务
- ✅ 改进服务商选择策略
- ✅ 完善错误处理机制

---

### 🤖 第四阶段：Agent 高级功能 (2025-04-06 ~ 2025-04-30)

#### v4.0 - AgentX-2025-04-06-feat-llm-service-agent
**发布日期**: 2025-04-06  
**版本主题**: LLM 服务与 Agent 深度集成

**主要内容**:
- ✅ Agent 与 LLM 服务深度集成
- ✅ Agent 策略框架
- ✅ 工具调用框架基础

**文件数量**: 175+ Java 文件

---

#### v4.1 - AgentX-2025-04-06-feat-llm-service-agent-2
**发布日期**: 2025-04-06  
**版本主题**: Agent 服务优化 v2

**主要内容**:
- ✅ 进一步优化 Agent 服务
- ✅ 改进工具调用机制

**文件数量**: 193 Java 文件

---

#### v4.2 - AgentX-2025-04-11-feat-plugin-market-tools
**发布日期**: 2025-04-11  
**版本主题**: 插件市场与工具系统

**主要内容**:
- ✅ 工具市场基础架构
- ✅ 工具分类与搜索
- ✅ 工具安装与配置
- ✅ 工具使用统计

**新增功能**:
- 工具库浏览
- 工具上传与验证
- 工具权限管理

---

#### v4.3 - AgentX-2025-04-18-feat-llm-service-agent-3
**发布日期**: 2025-04-18  
**版本主题**: Agent 服务优化 v3

---

#### v4.4 - AgentX-2025-04-20-feat-llm-service-agent-4
**发布日期**: 2025-04-20  
**版本主题**: Agent 服务优化 v4

---

#### v4.5 - AgentX-2025-04-30-feat-user
**发布日期**: 2025-04-30  
**版本主题**: 用户系统

**主要内容**:
- ✅ 用户注册/登录功能
- ✅ GitHub OAuth 集成
- ✅ Email 认证
- ✅ 用户权限管理
- ✅ 用户会话历史
- ✅ 用户偏好设置

**技术栈变化**:
- 升级到 Java 17+
- 升级到 Spring Boot 3.x

---

#### v4.6 - AgentX-2025-04-30-feat-user-dev
**发布日期**: 2025-04-30  
**版本主题**: 用户系统开发版本

---

### 🛠️ 第五阶段：工具与定时任务 (2025-05-10 ~ 2025-05-29)

#### v5.0 - AgentX-2025-05-10-feat-plguin-tools
**发布日期**: 2025-05-10  
**版本主题**: 插件工具系统

**主要内容**:
- ✅ 完善插件工具系统
- ✅ MCP (Multi-Capability Platform) 集成准备
- ✅ 工具调用优化

**文件数量**: 257 Java 文件

---

#### v5.1 - AgentX-2025-05-21-feat-tool-llm
**发布日期**: 2025-05-21  
**版本主题**: 工具与 LLM 深度集成

**主要内容**:
- ✅ 工具与 LLM 协同工作
- ✅ Function Calling 实现
- ✅ 工具执行结果处理

---

#### v5.2 - AgentX-2025-05-26-feat-agent-schedule
**发布日期**: 2025-05-26  
**版本主题**: Agent 定时任务

**主要内容**:
- ✅ 定时任务框架
- ✅ Agent 自动化执行
- ✅ 任务调度管理
- ✅ 定时任务配置

**核心功能**:
- 自动化 Agent 执行
- Cron 表达式支持
- 任务执行日志

**文件数量**: 281 Java 文件

---

#### v5.3 - AgentX-2025-05-29-feat-chat-preview
**发布日期**: 2025-05-29  
**版本主题**: 对话预览功能

**主要内容**:
- ✅ 对话预览界面
- ✅ 实时预览效果
- ✅ 消息渲染优化

---

### 🎨 第六阶段：多模态与高可用 (2025-05-31 ~ 2025-06-27)

#### v6.0 - AgentX-2025-05-31-feat-chat-multimodal
**发布日期**: 2025-05-31  
**版本主题**: 多模态对话支持

**主要内容**:
- ✅ 图片输入支持
- ✅ 文件上传功能
- ✅ 多模态消息渲染
- ✅ 富文本对话

**文件数量**: 304 Java 文件

---

#### v6.1 - AgentX-2025-06-08-feat-model-high-availability
**发布日期**: 2025-06-08  
**版本主题**: 模型高可用组件

**主要内容**:
- ✅ 模型高可用网关集成
- ✅ 自动故障切换
- ✅ 负载均衡
- ✅ 服务降级策略

**子仓库**:
- API-Premium-Gateway: 高可用网关组件

**配置项**:
```env
HIGH_AVAILABILITY_ENABLED=true
HIGH_AVAILABILITY_GATEWAY_URL=http://localhost:8081
```

---

#### v6.2 - AgentX-2025-06-10-feat-interrupt
**发布日期**: 2025-06-10  
**版本主题**: 中断控制

**主要内容**:
- ✅ 对话中断功能
- ✅ Agent 执行中断
- ✅ 任务取消机制

---

#### v6.3 - AgentX-2025-06-14-feat-open-api
**发布日期**: 2025-06-14  
**版本主题**: OpenAPI 支持

**主要内容**:
- ✅ RESTful API 完善
- ✅ API 文档自动生成
- ✅ API 密钥管理
- ✅ 外部系统集成接口

**API 特性**:
- SDK 开发支持
- Webhook 集成
- 使用情况监控

---

#### v6.4 - AgentX-2025-06-27-feat-docker-manager
**发布日期**: 2025-06-27  
**版本主题**: Docker 管理器

**主要内容**:
- ✅ MCP Server 容器化管理
- ✅ Docker 容器生命周期管理
- ✅ 一键部署脚本优化

---

### 🧠 第七阶段：知识图谱与日志追踪 (2025-07-21 ~ 2025-09-01)

#### v7.0 - AgentX-2025-07-21-feat-knowledge-graph
**发布日期**: 2025-07-21  
**版本主题**: 知识图谱（初版）

**主要内容**:
- 🔄 知识图谱基础架构
- 🔄 实体关系提取
- 🔄 图数据库集成准备

**文件数量**: 957 Java 文件

---

#### v7.1 - AgentX-2025-07-21-feat-knowledge-graphV1
**发布日期**: 2025-07-21  
**版本主题**: 知识图谱 V1

**主要内容**:
- 🔄 知识图谱功能完善
- 🔄 图谱可视化

**文件数量**: 2195 文件（包含 class 文件）

---

#### v7.2 - AgentX-2025-07-26-feat-product
**发布日期**: 2025-07-26  
**版本主题**: 产品化功能

**主要内容**:
- ✅ 产品完整性优化
- ✅ 用户体验改进
- ✅ 性能优化

---

#### v7.3 - AgentX-2025-07-31-feat-agent-log-trace
**发布日期**: 2025-07-31  
**版本主题**: Agent 日志追踪

**主要内容**:
- ✅ Agent 执行日志记录
- ✅ 调用链追踪
- ✅ 性能监控

---

#### v7.4 - AgentX-2025-08-13-feat-agent-trace-log
**发布日期**: 2025-08-13  
**版本主题**: Agent 追踪日志优化

---

#### v7.5 - AgentX-2025-09-01-feat-structured-logging
**发布日期**: 2025-09-01  
**版本主题**: 结构化日志

**主要内容**:
- ✅ 结构化日志输出
- ✅ 日志查询优化
- ✅ ELK 集成准备

---

### 🚀 第八阶段：高级特性 (2025-10-16 ~ 至今)

#### v8.0 - AgentX-2025-10-16-feat-long-term-memory
**发布日期**: 2025-10-16  
**版本主题**: 长期记忆

**主要内容**:
- ✅ Agent 长期记忆机制
- ✅ 记忆存储与检索
- ✅ 个性化记忆管理
- ✅ 记忆衰减策略

**技术实现**:
- 向量数据库存储
- 记忆重要性评分
- 记忆关联推理

---

#### v8.1 - AgentX-2025-10-17-feat-multi-agent
**发布日期**: 2025-10-17  
**版本主题**: 多 Agent 协作

**主要内容**:
- 🔄 多 Agent 通信协议
- 🔄 Agent 协作框架
- 🔄 任务分配与协调

**功能状态**: 开发中

---

#### v9.0 - AgentX-master (当前主分支)
**最后更新**: 2025-10-17+  
**版本主题**: 生产稳定版

**完整功能列表**:
- ✅ Agent 管理（创建/发布）
- ✅ LLM 上下文管理（滑动窗口，摘要算法）
- ✅ Agent 策略（MCP）
- ✅ 大模型服务商管理
- ✅ 用户系统（GitHub/Email 登录）
- ✅ 工具市场
- ✅ MCP Server Community
- ✅ MCP Gateway
- ✅ 预设工具
- ✅ Agent 定时任务
- ✅ Agent OpenAPI
- ✅ 模型高可用组件
- ✅ RAG（检索增强生成）
- ✅ 计费系统
- ✅ Agent 监控
- ✅ 嵌入网站组件
- ✅ 长期记忆
- 🔄 Multi Agent（开发中）
- 🔄 知识图谱（规划中）

**部署优化**:
- 🐳 All-in-One Docker 镜像
- 🔥 热更新开发模式
- 🌐 智能环境适配
- 📋 完善的配置管理

**子仓库生态**:
1. **API-Premium-Gateway**: 模型高可用网关
2. **mcp-gateway**: MCP 服务统一管理
3. **agent-mcp-community**: MCP Server 开源社区

---

## 核心技术演进

### 1. 架构演进
```
单体架构 → DDD 架构 → 微服务准备
简单对话 → Agent 系统 → Multi-Agent 协作
```

### 2. LLM 集成演进
```
单一服务商 → 服务商抽象 → 高可用网关
→ Token 管理 → 上下文优化 → 长期记忆
```

### 3. 工具系统演进
```
基础工具 → 工具市场 → MCP 集成
→ 插件化架构 → 社区生态
```

### 4. 用户体验演进
```
命令行 → Web UI → 流式对话 → 多模态
→ 实时预览 → 嵌入式组件
```

### 5. 运维部署演进
```
本地开发 → Docker Compose → All-in-One 镜像
→ 热更新 → 智能部署 → 生产级配置
```

---

## 技术债务与重构记录

### 数据库层面
- ❌ 不使用外键索引（DDD 原则）
- ✅ 逻辑删除设计
- ✅ JSON 字段类型处理

### 代码规范
- ❌ 禁止使用 Lombok
- ✅ 使用 MyBatis-Plus
- ✅ 自定义异常体系
- ✅ SpringBoot Test 测试

### 前端规范
- ✅ TypeScript 强类型
- ✅ Tailwind CSS
- ✅ Shadcn/ui 组件
- ✅ 统一 HTTP 客户端

---

## 开发里程碑

| 时间节点 | 里程碑事件 |
|---------|-----------|
| 2025-03-19 | 🎉 项目启动，完成基础对话功能 |
| 2025-03-20 | 🔥 会话管理与上下文功能上线 |
| 2025-03-21 | 🤖 Agent 核心功能发布 |
| 2025-03-25 | ⚡ Token 溢出策略优化 |
| 2025-03-26 | 🏗️ 服务商抽象架构完成 |
| 2025-04-11 | 🛠️ 工具市场上线 |
| 2025-04-30 | 👤 用户系统发布，升级 Java 17 |
| 2025-05-26 | ⏰ 定时任务功能上线 |
| 2025-06-08 | 🛡️ 模型高可用组件集成 |
| 2025-06-14 | 📡 OpenAPI 开放平台 |
| 2025-07-21 | 🧠 知识图谱探索 |
| 2025-09-01 | 📊 结构化日志系统 |
| 2025-10-16 | 💾 长期记忆功能发布 |
| 2025-10-17 | 🤝 Multi-Agent 开发启动 |

---

## 未来规划

### 短期目标 (Q1 2026)
- [ ] Multi-Agent 协作完善
- [ ] 知识图谱生产化
- [ ] 性能优化与压测
- [ ] 国际化支持

### 中期目标 (Q2-Q3 2026)
- [ ] 企业版功能
- [ ] 私有化部署方案
- [ ] 更多 LLM 服务商接入
- [ ] Agent 市场生态

### 长期愿景
- 打造领先的开源 Agent 平台
- 建立活跃的开发者社区
- 提供企业级 AI 解决方案
- 推动 AI Agent 技术普及

---

## 开发团队与社区

- 🎥 [B站视频教程](https://www.bilibili.com/video/BV1qaTWzPERJ/)
- 📖 [敲鸭社区](https://code.xhyovo.cn/)
- 🎯 [项目演示 PPT](https://needless-comparison.surge.sh)
- 💻 [GitHub 仓库](https://github.com/lucky-aeon/AgentX)

---

## 版本统计

| 统计项 | 数值 |
|-------|------|
| 总版本数 | 32+ |
| 开发周期 | 7个月+ |
| Java 文件数 | 780+ (master) |
| TypeScript 文件数 | 251+ (master) |
| 累计功能模块 | 15+ |
| 子仓库数量 | 3 |

---

## 致谢

感谢所有贡献者对 AgentX 项目的支持与贡献！

[![AgentX](https://contrib.rocks/image?repo=lucky-aeon/agentX)](https://contrib.rocks/image?repo=lucky-aeon/agentX)

---

**文档生成时间**: 2025-11-06  
**项目仓库**: https://github.com/lucky-aeon/AgentX  
**开源协议**: MIT License

