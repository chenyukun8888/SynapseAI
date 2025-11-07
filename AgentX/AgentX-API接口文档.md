# AgentX API 接口文档

> 基于 master 分支生成 | 更新时间: 2025-11-06

## 📖 文档说明

本文档基于 AgentX master 分支的完整接口文档，涵盖所有42个Controller的全部接口。

### 接口分组

- [1. 健康检查](#1-健康检查)
- [2. 用户认证与授权](#2-用户认证与授权)
- [3. 用户管理](#3-用户管理)
- [4. Agent 管理(用户端)](#4-agent-管理用户端)
- [5. Agent 会话与对话](#5-agent-会话与对话)
- [6. Agent 工作区](#6-agent-工作区)
- [7. Agent 知识库配置](#7-agent-知识库配置)
- [8. Agent 小组件](#8-agent-小组件)
- [9. Agent 执行追踪](#9-agent-执行追踪)
- [10. Agent 任务](#10-agent-任务)
- [11. 工具市场](#11-工具市场)
- [12. LLM 服务商](#12-llm-服务商)
- [13. RAG 知识库](#13-rag-知识库)
- [14. 记忆管理](#14-记忆管理)
- [15. 文件上传](#15-文件上传)
- [16. 定时任务](#16-定时任务)
- [17. API Key](#17-api-key)
- [18. 账户管理](#18-账户管理)
- [19. 商品管理](#19-商品管理)
- [20. 订单管理](#20-订单管理)
- [21. 支付管理](#21-支付管理)
- [22. 使用记录](#22-使用记录)
- [23. 认证配置](#23-认证配置)
- [24. 管理员-Agent](#24-管理员-agent)
- [25. 管理员-LLM](#25-管理员-llm)
- [26. 管理员-工具](#26-管理员-工具)
- [27. 管理员-商品](#27-管理员-商品)
- [28. 管理员-用户](#28-管理员-用户)
- [29. 管理员-RAG审核](#29-管理员-rag审核)
- [30. 管理员-订单](#30-管理员-订单)
- [31. 管理员-容器](#31-管理员-容器)
- [32. 管理员-容器模板](#32-管理员-容器模板)
- [33. 管理员-计费规则](#33-管理员-计费规则)
- [34. 管理员-认证配置](#34-管理员-认证配置)
- [35. 外部 API](#35-外部-api)
- [36. 公开小组件](#36-公开小组件)
- [37. 容器模板(用户)](#37-容器模板用户)

### 认证说明

**需要认证的接口**: 需在 Header 中携带 JWT Token
```
Authorization: Bearer {token}
```

**公开接口**: 无需认证即可访问

**API Key 认证**: 外部 API 接口需要使用 API Key
```
X-Api-Key: {your_api_key}
```

### 通用响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1699999999999
}
```

**状态码说明**:
- `200` - 操作成功
- `400` - 参数错误
- `401` - 未认证
- `403` - 权限不足
- `404` - 资源不存在
- `500` - 服务器错误

---

## 1. 健康检查

### 1.1 健康检查
- **接口**: `GET /health`
- **认证**: 否
- **描述**: 检查系统运行状态
- **响应**:
```json
{
  "code": 200,
  "message": "ok",
  "data": null
}
```

---

## 2. 用户认证与授权

### 2.1 用户登录
- **接口**: `POST /login`
- **认证**: 否
- **描述**: 用户通过邮箱和密码登录
- **请求体**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs..."
  }
}
```

### 2.2 用户注册
- **接口**: `POST /register`
- **认证**: 否
- **描述**: 新用户注册账号
- **请求体**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "code": "123456",
  "username": "John Doe"
}
```

### 2.3 获取图形验证码
- **接口**: `POST /get-captcha`
- **认证**: 否
- **描述**: 获取图形验证码用于注册或发送邮件验证码
- **响应**:
```json
{
  "data": {
    "uuid": "123e4567-e89b-12d3-a456-426614174000",
    "imageBase64": "data:image/png;base64,iVBORw0KG..."
  }
}
```

### 2.4 发送邮箱验证码
- **接口**: `POST /send-email-code`
- **认证**: 否
- **描述**: 发送邮箱验证码用于注册
- **请求体**:
```json
{
  "email": "user@example.com",
  "captchaUuid": "123e4567-e89b-12d3-a456-426614174000",
  "captchaCode": "ABCD"
}
```

### 2.5 发送重置密码验证码
- **接口**: `POST /send-reset-password-code`
- **认证**: 否
- **描述**: 发送重置密码邮箱验证码
- **请求体**: 同发送邮箱验证码

### 2.6 验证邮箱验证码
- **接口**: `POST /verify-email-code`
- **认证**: 否
- **描述**: 验证邮箱验证码是否有效
- **请求体**:
```json
{
  "email": "user@example.com",
  "code": "123456"
}
```

### 2.7 重置密码
- **接口**: `POST /reset-password`
- **认证**: 否
- **描述**: 重置用户密码
- **请求体**:
```json
{
  "email": "user@example.com",
  "newPassword": "newpassword123",
  "code": "123456"
}
```

### 2.8 SSO 登录URL
- **接口**: `GET /sso/{provider}/login`
- **认证**: 否
- **描述**: 获取SSO登录URL (支持 community、github 等)
- **参数**:
  - `provider` (path): SSO提供商
  - `redirectUrl` (query): 登录成功后的回调地址

### 2.9 SSO 登录回调
- **接口**: `GET /sso/{provider}/callback`
- **认证**: 否
- **描述**: SSO登录回调处理
- **参数**:
  - `provider` (path): SSO提供商
  - `code` (query): 授权码
- **响应**: 返回登录 token

---

## 3. 用户管理

### 3.1 获取用户信息
- **接口**: `GET /users`
- **认证**: 是
- **描述**: 获取当前登录用户的信息
- **响应**:
```json
{
  "data": {
    "id": "user123",
    "username": "John Doe",
    "email": "user@example.com",
    "avatar": "https://...",
    "createdAt": "2025-01-01T00:00:00Z"
  }
}
```

### 3.2 修改用户信息
- **接口**: `POST /users`
- **认证**: 是
- **描述**: 修改当前用户信息
- **请求体**:
```json
{
  "username": "New Name",
  "avatar": "https://..."
}
```

### 3.3 修改密码
- **接口**: `PUT /users/password`
- **认证**: 是
- **描述**: 修改当前用户密码
- **请求体**:
```json
{
  "oldPassword": "old123",
  "newPassword": "new123"
}
```

### 3.4 获取用户设置
- **接口**: `GET /users/settings`
- **认证**: 是
- **描述**: 获取用户设置信息

### 3.5 更新用户设置
- **接口**: `PUT /users/settings`
- **认证**: 是
- **描述**: 更新用户设置

### 3.6 获取用户默认模型
- **接口**: `GET /users/settings/default-model`
- **认证**: 是
- **描述**: 获取用户默认模型ID

### 3.7 获取OCR模型列表
- **接口**: `GET /users/settings/ocr-models`
- **认证**: 是
- **描述**: 获取可用的OCR模型列表

### 3.8 获取嵌入模型列表
- **接口**: `GET /users/settings/embedding-models`
- **认证**: 是
- **描述**: 获取可用的嵌入模型列表

---

## 4. Agent 管理(用户端)

### 4.1 创建 Agent
- **接口**: `POST /agents`
- **认证**: 是
- **描述**: 创建新的 Agent
- **请求体**:
```json
{
  "name": "我的智能助手",
  "avatar": "https://...",
  "description": "这是一个智能助手",
  "systemPrompt": "你是一个友善的助手...",
  "welcomeMessage": "你好！我能帮你什么？",
  "modelConfig": {
    "providerId": "provider123",
    "modelId": "model123",
    "temperature": 0.7,
    "maxTokens": 2000
  },
  "tools": [
    {
      "toolId": "tool123",
      "enabled": true
    }
  ]
}
```

### 4.2 获取 Agent 详情
- **接口**: `GET /agents/{agentId}`
- **认证**: 是
- **描述**: 获取指定 Agent 的详细信息

### 4.3 获取用户的 Agent 列表
- **接口**: `GET /agents/user`
- **认证**: 是
- **描述**: 获取当前用户创建的所有 Agent
- **参数**:
  - `name` (query): Agent名称(模糊搜索)
  - `enabled` (query): 启用状态(true/false)

### 4.4 获取已上架的 Agent 列表
- **接口**: `GET /agents/published`
- **认证**: 是
- **描述**: 获取所有已发布的 Agent
- **参数**:
  - `name` (query): Agent名称(模糊搜索)

### 4.5 更新 Agent
- **接口**: `PUT /agents/{agentId}`
- **认证**: 是
- **描述**: 更新 Agent 基本信息和配置

### 4.6 切换 Agent 启用状态
- **接口**: `PUT /agents/{agentId}/toggle-status`
- **认证**: 是
- **描述**: 启用或禁用 Agent

### 4.7 删除 Agent
- **接口**: `DELETE /agents/{agentId}`
- **认证**: 是
- **描述**: 软删除 Agent

### 4.8 发布 Agent 版本
- **接口**: `POST /agents/{agentId}/publish`
- **认证**: 是
- **描述**: 提交 Agent 版本供审核
- **请求体**:
```json
{
  "versionNumber": "1.0.0",
  "changeLog": "初始版本发布"
}
```

### 4.9 获取 Agent 所有版本
- **接口**: `GET /agents/{agentId}/versions`
- **认证**: 是
- **描述**: 获取指定 Agent 的所有版本列表

### 4.10 获取 Agent 特定版本
- **接口**: `GET /agents/{agentId}/versions/{versionNumber}`
- **认证**: 是
- **描述**: 获取指定版本的详细信息

### 4.11 获取 Agent 最新版本
- **接口**: `GET /agents/{agentId}/versions/latest`
- **认证**: 是
- **描述**: 获取 Agent 的最新版本

### 4.12 生成系统提示词
- **接口**: `POST /agents/generate-system-prompt`
- **认证**: 是
- **描述**: 使用 AI 生成系统提示词
- **请求体**:
```json
{
  "description": "我想创建一个编程助手",
  "requirements": ["精通Python", "能够debug"]
}
```

---

## 5. Agent 会话与对话

### 5.1 获取会话消息列表
- **接口**: `GET /agents/sessions/{sessionId}/messages`
- **认证**: 是
- **描述**: 获取指定会话的所有消息

### 5.2 获取 Agent 会话列表
- **接口**: `GET /agents/sessions/{agentId}`
- **认证**: 是
- **描述**: 获取指定 Agent 的所有会话

### 5.3 创建会话
- **接口**: `POST /agents/sessions/{agentId}`
- **认证**: 是
- **描述**: 为指定 Agent 创建新会话

### 5.4 更新会话
- **接口**: `PUT /agents/sessions/{id}`
- **认证**: 是
- **描述**: 更新会话标题
- **参数**:
  - `title` (query): 新标题

### 5.5 删除会话
- **接口**: `DELETE /agents/sessions/{id}`
- **认证**: 是
- **描述**: 删除指定会话

### 5.6 发送消息(流式)
- **接口**: `POST /agents/sessions/chat`
- **认证**: 是
- **描述**: 在会话中发送消息，使用 SSE 流式返回 AI 回复
- **响应类型**: `text/event-stream`
- **请求体**:
```json
{
  "sessionId": "session123",
  "message": "你好",
  "fileUrls": ["https://..."]
}
```

### 5.7 Agent 预览对话
- **接口**: `POST /agents/sessions/preview`
- **认证**: 是
- **描述**: 预览 Agent 对话效果，不保存会话
- **响应类型**: `text/event-stream`

### 5.8 中断对话
- **接口**: `POST /agents/sessions/{sessionId}/interrupt`
- **认证**: 是
- **描述**: 中断正在进行的对话

---

## 6. Agent 工作区

### 6.1 获取工作区助理列表
- **接口**: `GET /agents/workspaces/agents`
- **认证**: 是
- **描述**: 获取工作区下的助理列表

### 6.2 删除工作区助理
- **接口**: `DELETE /agents/workspaces/agents/{id}`
- **认证**: 是
- **描述**: 从工作区删除助理

### 6.3 设置模型配置
- **接口**: `PUT /agents/workspaces/{agentId}/model/config`
- **认证**: 是
- **描述**: 设置 Agent 的模型配置

### 6.4 获取模型配置
- **接口**: `GET /agents/workspaces/{agentId}/model-config`
- **认证**: 是
- **描述**: 获取 Agent 的模型配置

### 6.5 添加助理到工作区
- **接口**: `POST /agents/workspaces/{agentId}`
- **认证**: 是
- **描述**: 将助理添加到工作区

---

## 7. Agent 知识库配置

### 7.1 获取可用知识库列表
- **接口**: `GET /agents/knowledge-bases/available`
- **认证**: 是
- **描述**: 获取用户可用的知识库列表(用于 Agent 配置)

### 7.2 获取知识库详情
- **接口**: `GET /agents/knowledge-bases/{knowledgeBaseId}`
- **认证**: 是
- **描述**: 获取知识库详情

### 7.3 批量获取知识库详情
- **接口**: `GET /agents/knowledge-bases/batch`
- **认证**: 是
- **描述**: 批量获取知识库详情
- **参数**:
  - `knowledgeBaseIds` (query): 知识库ID列表，用逗号分隔

---

## 8. Agent 小组件

### 8.1 创建小组件配置
- **接口**: `POST /agents/{agentId}/widgets`
- **认证**: 是
- **描述**: 创建小组件配置

### 8.2 获取小组件配置列表
- **接口**: `GET /agents/{agentId}/widgets`
- **认证**: 是
- **描述**: 获取 Agent 的所有小组件配置

### 8.3 获取小组件配置详情
- **接口**: `GET /agents/{agentId}/widgets/{widgetId}`
- **认证**: 是
- **描述**: 获取小组件配置详情

### 8.4 更新小组件配置
- **接口**: `PUT /agents/{agentId}/widgets/{widgetId}`
- **认证**: 是
- **描述**: 更新小组件配置

### 8.5 切换小组件状态
- **接口**: `POST /agents/{agentId}/widgets/{widgetId}/status`
- **认证**: 是
- **描述**: 启用/禁用小组件

### 8.6 删除小组件配置
- **接口**: `DELETE /agents/{agentId}/widgets/{widgetId}`
- **认证**: 是
- **描述**: 删除小组件配置

### 8.7 获取用户的所有小组件
- **接口**: `GET /user/widgets`
- **认证**: 是
- **描述**: 获取用户的所有小组件配置

---

## 9. Agent 执行追踪

### 9.1 分页查询执行历史
- **接口**: `GET /traces/history`
- **认证**: 是
- **描述**: 分页查询用户执行历史
- **参数**:
  - `page` (query): 页码
  - `pageSize` (query): 每页数量
  - `agentId` (query): Agent ID筛选
  - `status` (query): 状态筛选

### 9.2 获取追踪详情
- **接口**: `GET /traces/{traceId}`
- **认证**: 是
- **描述**: 获取单个追踪详情

### 9.3 获取执行详情列表
- **接口**: `GET /traces/{traceId}/details`
- **认证**: 是
- **描述**: 获取执行详情列表

### 9.4 查询会话执行记录
- **接口**: `GET /traces/sessions/{sessionId}`
- **认证**: 是
- **描述**: 查询会话执行记录

### 9.5 获取执行统计
- **接口**: `GET /traces/statistics`
- **认证**: 是
- **描述**: 获取用户执行统计信息

### 9.6 获取 Agent 追踪统计
- **接口**: `GET /traces/agents`
- **认证**: 是
- **描述**: 获取用户的 Agent 执行链路统计信息

### 9.7 获取会话追踪统计
- **接口**: `GET /traces/agents/{agentId}/sessions`
- **认证**: 是
- **描述**: 获取指定 Agent 下的会话执行链路统计信息

---

## 10. Agent 任务

### 10.1 获取会话任务
- **接口**: `GET /tasks/session/{sessionId}/latest`
- **认证**: 是
- **描述**: 获取当前会话的最新任务

---

## 11. 工具市场

### 11.1 上传工具
- **接口**: `POST /tools`
- **认证**: 是
- **描述**: 上传新工具到平台
- **请求体**:
```json
{
  "name": "我的工具",
  "description": "工具描述",
  "uploadType": "github",
  "githubUrl": "https://github.com/user/repo"
}
```

### 11.2 获取工具详情
- **接口**: `GET /tools/{toolId}`
- **认证**: 是
- **描述**: 获取指定工具的详细信息

### 11.3 获取用户的工具列表
- **接口**: `GET /tools/user`
- **认证**: 是
- **描述**: 获取当前用户上传的所有工具

### 11.4 编辑工具
- **接口**: `PUT /tools/{toolId}`
- **认证**: 是
- **描述**: 更新工具信息

### 11.5 删除工具
- **接口**: `DELETE /tools/{toolId}`
- **认证**: 是
- **描述**: 删除指定工具

### 11.6 上架工具
- **接口**: `POST /tools/market`
- **认证**: 是
- **描述**: 将工具上架到市场
- **请求体**:
```json
{
  "toolId": "tool123",
  "versionNumber": "1.0.0",
  "changeLog": "初始版本"
}
```

### 11.7 工具市场列表
- **接口**: `GET /tools/market`
- **认证**: 是
- **描述**: 获取工具市场列表
- **参数**:
  - `page`, `pageSize`, `keyword`

### 11.8 获取工具版本详情
- **接口**: `GET /tools/market/{toolId}/{version}`
- **认证**: 是
- **描述**: 获取工具特定版本的详情

### 11.9 安装工具
- **接口**: `POST /tools/install/{toolId}/{version}`
- **认证**: 是
- **描述**: 安装指定版本的工具

### 11.10 卸载工具
- **接口**: `POST /tools/uninstall/{toolId}`
- **认证**: 是
- **描述**: 卸载工具

### 11.11 获取已安装的工具列表
- **接口**: `GET /tools/installed`
- **认证**: 是
- **描述**: 获取已安装的工具列表

### 11.12 获取工具的所有版本
- **接口**: `GET /tools/market/{toolId}/versions`
- **认证**: 是
- **描述**: 获取工具已发布的所有版本

### 11.13 获取推荐工具
- **接口**: `GET /tools/recommend`
- **认证**: 是
- **描述**: 获取推荐工具列表

### 11.14 修改工具版本发布状态
- **接口**: `POST /tools/user/{toolId}/{version}/status`
- **认证**: 是
- **描述**: 发布或下架工具版本
- **参数**:
  - `publishStatus` (query): 发布状态 (true/false)

### 11.15 获取工具最新版本
- **接口**: `GET /tools/{toolId}/latest`
- **认证**: 是
- **描述**: 获取工具最新版本号

---

## 12. LLM 服务商

### 12.1 获取服务商详情
- **接口**: `GET /llms/providers/{providerId}`
- **认证**: 是
- **描述**: 获取指定服务商的详细信息

### 12.2 获取服务商列表
- **接口**: `GET /llms/providers`
- **认证**: 是
- **描述**: 获取 LLM 服务商列表
- **参数**:
  - `type` (query): 服务商类型 (all/official/user)

### 12.3 创建服务商
- **接口**: `POST /llms/providers`
- **认证**: 是
- **描述**: 创建新的 LLM 服务商

### 12.4 更新服务商
- **接口**: `PUT /llms/providers`
- **认证**: 是
- **描述**: 更新 LLM 服务商信息

### 12.5 修改服务商状态
- **接口**: `POST /llms/providers/{providerId}/status`
- **认证**: 是
- **描述**: 启用/禁用服务商

### 12.6 删除服务商
- **接口**: `DELETE /llms/providers/{providerId}`
- **认证**: 是
- **描述**: 删除服务提供商

### 12.7 获取协议列表
- **接口**: `GET /llms/providers/protocols`
- **认证**: 是
- **描述**: 获取支持的协议列表

### 12.8 添加模型
- **接口**: `POST /llms/models`
- **认证**: 是
- **描述**: 添加新模型

### 12.9 修改模型
- **接口**: `PUT /llms/models`
- **认证**: 是
- **描述**: 更新模型信息

### 12.10 删除模型
- **接口**: `DELETE /llms/models/{modelId}`
- **认证**: 是
- **描述**: 删除模型

### 12.11 修改模型状态
- **接口**: `PUT /llms/models/{modelId}/status`
- **认证**: 是
- **描述**: 启用/禁用模型

### 12.12 获取模型类型
- **接口**: `GET /llms/models/types`
- **认证**: 是
- **描述**: 获取所有支持的模型类型

### 12.13 获取所有激活模型
- **接口**: `GET /llms/models`
- **认证**: 是
- **描述**: 获取激活的模型列表
- **参数**:
  - `modelType` (query): 模型类型
  - `official` (query): 是否只获取官方模型

### 12.14 获取默认模型
- **接口**: `GET /llms/models/default`
- **认证**: 是
- **描述**: 获取用户默认的模型详情

---

## 13. RAG 知识库

### 13.1 RAG 搜索文档
- **接口**: `POST /rag/search`
- **认证**: 是
- **描述**: 在知识库中搜索相关文档
- **请求体**:
```json
{
  "query": "搜索关键词",
  "topK": 5,
  "ragIds": ["rag123", "rag456"]
}
```

### 13.2 RAG 流式问答
- **接口**: `POST /rag/search/stream-chat`
- **认证**: 是
- **描述**: 基于知识库进行流式问答
- **响应类型**: `text/event-stream`

### 13.3 基于用户 RAG 搜索
- **接口**: `POST /rag/search/user-rag/{userRagId}`
- **认证**: 是
- **描述**: 基于已安装知识库的RAG搜索

### 13.4 基于用户 RAG 流式问答
- **接口**: `POST /rag/search/user-rag/{userRagId}/stream-chat`
- **认证**: 是
- **描述**: 基于已安装知识库的RAG流式问答
- **响应类型**: `text/event-stream`

### 13.5 获取 RAG 市场列表
- **接口**: `GET /rag/market`
- **认证**: 是
- **描述**: 获取市场上的RAG版本列表

### 13.6 安装 RAG 版本
- **接口**: `POST /rag/market/install`
- **认证**: 是
- **描述**: 安装RAG版本

### 13.7 卸载 RAG 版本
- **接口**: `DELETE /rag/market/uninstall/{ragVersionId}`
- **认证**: 是
- **描述**: 卸载RAG版本

### 13.8 获取已安装的 RAG 列表
- **接口**: `GET /rag/market/installed`
- **认证**: 是
- **描述**: 获取用户安装的RAG列表(分页)

### 13.9 获取所有已安装的 RAG
- **接口**: `GET /rag/market/installed/all`
- **认证**: 是
- **描述**: 获取用户安装的所有RAG(用于对话中选择)

### 13.10 获取已安装 RAG 详情
- **接口**: `GET /rag/market/installed/{ragVersionId}`
- **认证**: 是
- **描述**: 获取已安装RAG详情

### 13.11 检查 RAG 使用权限
- **接口**: `GET /rag/market/permission/check`
- **认证**: 是
- **描述**: 检查用户是否有权限使用RAG
- **参数**:
  - `ragId`, `ragVersionId`

### 13.12 切换 RAG 版本
- **接口**: `PUT /rag/market/installed/{userRagId}/switch-version`
- **认证**: 是
- **描述**: 切换已安装RAG的版本

### 13.13 获取已安装 RAG 版本列表
- **接口**: `GET /rag/market/installed/{userRagId}/versions`
- **认证**: 是
- **描述**: 获取同一RAG的所有已安装版本

### 13.14 获取已安装 RAG 文件列表
- **接口**: `GET /rag/market/installed/{userRagId}/files`
- **认证**: 是
- **描述**: 获取已安装RAG的文件列表

### 13.15 获取已安装 RAG 文档单元
- **接口**: `GET /rag/market/installed/{userRagId}/documents`
- **认证**: 是
- **描述**: 获取已安装RAG的所有文档单元

### 13.16 获取已安装 RAG 文件信息
- **接口**: `GET /rag/market/installed/{userRagId}/files/{fileId}/info`
- **认证**: 是
- **描述**: 获取已安装RAG特定文件的信息

### 13.17 获取已安装 RAG 文件文档单元
- **接口**: `GET /rag/market/installed/{userRagId}/files/{fileId}/documents`
- **认证**: 是
- **描述**: 获取已安装RAG特定文件的文档单元

### 13.18 获取市场 RAG 文件列表
- **接口**: `GET /rag/market/{ragVersionId}/files`
- **认证**: 是
- **描述**: 获取市场上RAG版本的文件列表

### 13.19 发布 RAG 版本
- **接口**: `POST /rag/publish`
- **认证**: 是
- **描述**: 发布RAG版本

### 13.20 获取用户的 RAG 版本列表
- **接口**: `GET /rag/publish/versions`
- **认证**: 是
- **描述**: 获取用户的RAG版本列表(分页)

### 13.21 获取 RAG 版本历史
- **接口**: `GET /rag/publish/versions/history/{ragId}`
- **认证**: 是
- **描述**: 获取RAG的版本历史

### 13.22 获取 RAG 版本详情
- **接口**: `GET /rag/publish/versions/{versionId}`
- **认证**: 是
- **描述**: 获取RAG版本详情

### 13.23 获取 RAG 最新版本号
- **接口**: `GET /rag/publish/versions/latest/{ragId}`
- **认证**: 是
- **描述**: 获取RAG数据集的最新版本号

### 13.24 创建数据集
- **接口**: `POST /rag/datasets`
- **认证**: 是
- **描述**: 创建RAG数据集

### 13.25 更新数据集
- **接口**: `PUT /rag/datasets/{datasetId}`
- **认证**: 是
- **描述**: 更新RAG数据集

### 13.26 删除数据集
- **接口**: `DELETE /rag/datasets/{datasetId}`
- **认证**: 是
- **描述**: 删除RAG数据集

### 13.27 获取数据集详情
- **接口**: `GET /rag/datasets/{datasetId}`
- **认证**: 是
- **描述**: 获取数据集详情

### 13.28 分页查询数据集
- **接口**: `GET /rag/datasets`
- **认证**: 是
- **描述**: 分页查询数据集

### 13.29 获取所有数据集
- **接口**: `GET /rag/datasets/all`
- **认证**: 是
- **描述**: 获取所有数据集(不分页)

### 13.30 上传文件到数据集
- **接口**: `POST /rag/datasets/files`
- **认证**: 是
- **描述**: 上传文件到数据集

### 13.31 删除数据集文件
- **接口**: `DELETE /rag/datasets/{datasetId}/files/{fileId}`
- **认证**: 是
- **描述**: 删除数据集文件

### 13.32 分页查询数据集文件
- **接口**: `GET /rag/datasets/{datasetId}/files`
- **认证**: 是
- **描述**: 分页查询数据集文件

### 13.33 获取数据集所有文件
- **接口**: `GET /rag/datasets/{datasetId}/files/all`
- **认证**: 是
- **描述**: 获取数据集所有文件(不分页)

### 13.34 启动文件预处理
- **接口**: `POST /rag/datasets/files/process`
- **认证**: 是
- **描述**: 启动文件预处理(手动触发)

### 13.35 重新启动文件预处理
- **接口**: `POST /rag/datasets/files/reprocess`
- **认证**: 是
- **描述**: 重新启动文件预处理(强制重启)

### 13.36 获取文件处理进度
- **接口**: `GET /rag/datasets/files/{fileId}/progress`
- **认证**: 是
- **描述**: 获取文件处理进度

### 13.37 获取数据集文件处理进度列表
- **接口**: `GET /rag/datasets/{datasetId}/files/progress`
- **认证**: 是
- **描述**: 获取数据集文件处理进度列表

### 13.38 获取文件详细信息
- **接口**: `GET /rag/files/{fileId}/info`
- **认证**: 是
- **描述**: 根据文件ID获取文件详细信息

### 13.39 批量删除文件
- **接口**: `POST /rag/files/batch-delete`
- **认证**: 是
- **描述**: 批量删除文件

### 13.40 分页查询文件语料
- **接口**: `POST /rag/files/document-units/list`
- **认证**: 是
- **描述**: 分页查询文件的语料

### 13.41 更新语料内容
- **接口**: `PUT /rag/files/document-units`
- **认证**: 是
- **描述**: 更新语料内容

### 13.42 删除语料
- **接口**: `DELETE /rag/files/document-units/{documentUnitId}`
- **认证**: 是
- **描述**: 删除语料

### 13.43 获取语料详情
- **接口**: `GET /rag/files/document-units/{documentUnitId}`
- **认证**: 是
- **描述**: 根据语料ID获取单个语料详情

---

## 14. 记忆管理

### 14.1 分页列出记忆
- **接口**: `GET /portal/memory/items`
- **认证**: 是
- **描述**: 分页列出当前用户的记忆(可选类型过滤)

### 14.2 手动新增记忆
- **接口**: `POST /portal/memory/items`
- **认证**: 是
- **描述**: 手动新增记忆(立即入库并向量化)

### 14.3 归档记忆
- **接口**: `DELETE /portal/memory/items/{itemId}`
- **认证**: 是
- **描述**: 归档(软删除)记忆

---

## 15. 文件上传

### 15.1 获取上传凭证
- **接口**: `GET /upload/credential`
- **认证**: 是
- **描述**: 获取前端直传OSS的上传凭证

---

## 16. 定时任务

### 16.1 创建定时任务
- **接口**: `POST /scheduled-tasks`
- **认证**: 是
- **描述**: 创建定时任务

### 16.2 更新定时任务
- **接口**: `PUT /scheduled-tasks/{taskId}`
- **认证**: 是
- **描述**: 更新定时任务

### 16.3 删除定时任务
- **接口**: `DELETE /scheduled-tasks/{taskId}`
- **认证**: 是
- **描述**: 删除定时任务

### 16.4 获取定时任务列表
- **接口**: `GET /scheduled-tasks`
- **认证**: 是
- **描述**: 获取用户的定时任务列表
- **参数**:
  - `sessionId` (query): 会话ID筛选
  - `agentId` (query): Agent ID筛选

### 16.5 根据 Agent 获取任务
- **接口**: `GET /scheduled-tasks/agent/{agentId}`
- **认证**: 是
- **描述**: 根据Agent ID获取定时任务列表

### 16.6 获取单个定时任务
- **接口**: `GET /scheduled-tasks/{taskId}`
- **认证**: 是
- **描述**: 获取单个定时任务详情

### 16.7 暂停定时任务
- **接口**: `POST /scheduled-tasks/{taskId}/pause`
- **认证**: 是
- **描述**: 暂停定时任务

### 16.8 恢复定时任务
- **接口**: `POST /scheduled-tasks/{taskId}/resume`
- **认证**: 是
- **描述**: 恢复定时任务

---

## 17. API Key

### 17.1 创建 API Key
- **接口**: `POST /api-keys`
- **认证**: 是
- **描述**: 创建API密钥

### 17.2 获取 API Key 列表
- **接口**: `GET /api-keys`
- **认证**: 是
- **描述**: 获取用户的API密钥列表

### 17.3 获取 Agent 的 API Key
- **接口**: `GET /api-keys/agent/{agentId}`
- **认证**: 是
- **描述**: 获取Agent的API密钥列表

### 17.4 获取 API Key 详情
- **接口**: `GET /api-keys/{apiKeyId}`
- **认证**: 是
- **描述**: 获取API密钥详情

### 17.5 更新 API Key 状态
- **接口**: `PUT /api-keys/{apiKeyId}/status`
- **认证**: 是
- **描述**: 更新API密钥状态

### 17.6 删除 API Key
- **接口**: `DELETE /api-keys/{apiKeyId}`
- **认证**: 是
- **描述**: 删除API密钥

### 17.7 重置 API Key
- **接口**: `POST /api-keys/{apiKeyId}/reset`
- **认证**: 是
- **描述**: 重置API密钥

---

## 18. 账户管理

### 18.1 获取当前用户账户
- **接口**: `GET /accounts/current`
- **认证**: 是
- **描述**: 获取当前用户账户信息

---

## 19. 商品管理

### 19.1 根据ID获取商品
- **接口**: `GET /products/{productId}`
- **认证**: 是
- **描述**: 根据ID获取商品详情

### 19.2 根据业务标识获取商品
- **接口**: `GET /products/business`
- **认证**: 是
- **描述**: 根据业务标识获取商品
- **参数**:
  - `type` (query): 计费类型
  - `serviceId` (query): 服务ID

### 19.3 获取活跃商品列表
- **接口**: `GET /products/active`
- **认证**: 是
- **描述**: 获取指定类型的活跃商品列表
- **参数**:
  - `type` (query): 计费类型(可选)

### 19.4 检查商品是否活跃
- **接口**: `GET /products/business/active`
- **认证**: 是
- **描述**: 检查商品是否存在且激活
- **参数**:
  - `type`, `serviceId`

---

## 20. 订单管理

### 20.1 获取用户订单列表
- **接口**: `GET /orders`
- **认证**: 是
- **描述**: 获取当前用户的已支付订单列表(分页)

### 20.2 获取订单详情
- **接口**: `GET /orders/{orderId}`
- **认证**: 是
- **描述**: 获取订单详情

---

## 21. 支付管理

### 21.1 创建充值支付
- **接口**: `POST /payments/recharge`
- **认证**: 是
- **描述**: 创建充值支付
- **请求体**:
```json
{
  "amount": 100.00,
  "paymentPlatform": "alipay",
  "paymentType": "native"
}
```

### 21.2 查询订单状态
- **接口**: `GET /payments/orders/{orderNo}/status`
- **认证**: 是
- **描述**: 查询订单状态

### 21.3 获取支付方法列表
- **接口**: `GET /payments/methods`
- **认证**: 是
- **描述**: 获取可用的支付方法列表

### 21.4 支付平台回调
- **接口**: `POST /payments/callback/{platform}`
- **认证**: 否
- **描述**: 处理支付平台回调

---

## 22. 使用记录

### 22.1 根据ID获取使用记录
- **接口**: `GET /usage-records/{recordId}`
- **认证**: 是
- **描述**: 根据ID获取使用记录

### 22.2 查询使用记录
- **接口**: `GET /usage-records`
- **认证**: 是
- **描述**: 按条件查询当前用户使用记录(分页)

### 22.3 获取总消费金额
- **接口**: `GET /usage-records/current/total-cost`
- **认证**: 是
- **描述**: 获取当前用户的总消费金额

---

## 23. 认证配置

### 23.1 获取认证配置
- **接口**: `GET /auth/config`
- **认证**: 否
- **描述**: 获取可用的认证配置

---

## 24. 管理员-Agent

### 24.1 分页获取 Agent 列表
- **接口**: `GET /admin/agents`
- **认证**: 是(管理员)
- **描述**: 分页获取Agent列表

### 24.2 获取 Agent 统计
- **接口**: `GET /admin/agents/statistics`
- **认证**: 是(管理员)
- **描述**: 获取Agent统计信息

### 24.3 获取版本列表
- **接口**: `GET /admin/agents/versions`
- **认证**: 是(管理员)
- **描述**: 获取版本列表，可按状态或Agent筛选
- **参数**:
  - `status` (query): 版本状态 (1-审核中，2-已发布，3-已拒绝，4-已下架)
  - `agentId` (query): Agent ID

### 24.4 更新版本状态
- **接口**: `POST /admin/agents/versions/{versionId}/status`
- **认证**: 是(管理员)
- **描述**: 审核通过/拒绝/下架 Agent 版本
- **参数**:
  - `status` (query): 目标状态 (1-4)
  - `reason` (query): 拒绝原因(拒绝时必填)

---

## 25. 管理员-LLM

### 25.1 获取服务商列表
- **接口**: `GET /admin/llms/providers`
- **认证**: 是(管理员)
- **描述**: 获取服务商列表
- **参数**:
  - `page`, `pageSize`

### 25.2 获取服务商详情
- **接口**: `GET /admin/llms/providers/{providerId}`
- **认证**: 是(管理员)
- **描述**: 获取服务商详情

### 25.3 创建服务商
- **接口**: `POST /admin/llms/providers`
- **认证**: 是(管理员)
- **描述**: 创建服务商

### 25.4 更新服务商
- **接口**: `PUT /admin/llms/providers/{id}`
- **认证**: 是(管理员)
- **描述**: 更新服务商

### 25.5 切换服务商状态
- **接口**: `POST /admin/llms/providers/{id}/status`
- **认证**: 是(管理员)
- **描述**: 切换服务商状态

### 25.6 删除服务商
- **接口**: `DELETE /admin/llms/providers/{id}`
- **认证**: 是(管理员)
- **描述**: 删除服务商

### 25.7 获取协议列表
- **接口**: `GET /admin/llms/providers/protocols`
- **认证**: 是(管理员)
- **描述**: 获取支持的协议列表

### 25.8 获取模型列表
- **接口**: `GET /admin/llms/models`
- **认证**: 是(管理员)
- **描述**: 获取模型列表
- **参数**:
  - `providerId`, `modelType`, `page`, `pageSize`

### 25.9 创建模型
- **接口**: `POST /admin/llms/models`
- **认证**: 是(管理员)
- **描述**: 创建模型

### 25.10 更新模型
- **接口**: `PUT /admin/llms/models/{id}`
- **认证**: 是(管理员)
- **描述**: 更新模型

### 25.11 切换模型状态
- **接口**: `POST /admin/llms/models/{id}/status`
- **认证**: 是(管理员)
- **描述**: 切换模型状态

### 25.12 删除模型
- **接口**: `DELETE /admin/llms/models/{id}`
- **认证**: 是(管理员)
- **描述**: 删除模型

### 25.13 获取模型类型列表
- **接口**: `GET /admin/llms/models/types`
- **认证**: 是(管理员)
- **描述**: 获取模型类型列表

---

## 26. 管理员-工具

### 26.1 分页获取工具列表
- **接口**: `GET /admin/tools`
- **认证**: 是(管理员)
- **描述**: 分页获取工具列表

### 26.2 获取工具统计
- **接口**: `GET /admin/tools/statistics`
- **认证**: 是(管理员)
- **描述**: 获取工具统计信息

### 26.3 创建官方工具
- **接口**: `POST /admin/tools/official`
- **认证**: 是(管理员)
- **描述**: 创建官方工具

### 26.4 修改工具状态
- **接口**: `POST /admin/tools/{toolId}/status`
- **认证**: 是(管理员)
- **描述**: 修改工具的状态
- **参数**:
  - `status`: 工具状态
  - `reason`: 拒绝原因(可选)

### 26.5 更新工具全局状态
- **接口**: `PUT /admin/tools/{toolId}/global-status`
- **认证**: 是(管理员)
- **描述**: 更新工具全局状态

---

## 27. 管理员-商品

### 27.1 分页获取商品列表
- **接口**: `GET /admin/products`
- **认证**: 是(管理员)
- **描述**: 分页获取商品列表

### 27.2 获取所有商品
- **接口**: `GET /admin/products/all`
- **认证**: 是(管理员)
- **描述**: 获取所有商品列表(不分页)

### 27.3 根据ID获取商品
- **接口**: `GET /admin/products/{productId}`
- **认证**: 是(管理员)
- **描述**: 根据ID获取商品详情

### 27.4 根据业务标识获取商品
- **接口**: `GET /admin/products/business`
- **认证**: 是(管理员)
- **描述**: 根据业务标识获取商品

### 27.5 创建商品
- **接口**: `POST /admin/products`
- **认证**: 是(管理员)
- **描述**: 创建商品

### 27.6 更新商品
- **接口**: `PUT /admin/products/{productId}`
- **认证**: 是(管理员)
- **描述**: 更新商品

### 27.7 删除商品
- **接口**: `DELETE /admin/products/{productId}`
- **认证**: 是(管理员)
- **描述**: 删除商品

### 27.8 启用商品
- **接口**: `POST /admin/products/{productId}/enable`
- **认证**: 是(管理员)
- **描述**: 启用商品

### 27.9 禁用商品
- **接口**: `POST /admin/products/{productId}/disable`
- **认证**: 是(管理员)
- **描述**: 禁用商品

### 27.10 检查商品是否存在
- **接口**: `GET /admin/products/{productId}/exists`
- **认证**: 是(管理员)
- **描述**: 检查商品是否存在

### 27.11 检查业务标识是否存在
- **接口**: `GET /admin/products/business/exists`
- **认证**: 是(管理员)
- **描述**: 检查业务标识是否存在

---

## 28. 管理员-用户

### 28.1 分页获取用户列表
- **接口**: `GET /admin/users`
- **认证**: 是(管理员)
- **描述**: 分页获取用户列表

---

## 29. 管理员-RAG审核

### 29.1 获取待审核列表
- **接口**: `GET /admin/rags/pending`
- **认证**: 是(管理员)
- **描述**: 获取待审核的RAG版本列表

### 29.2 审核 RAG 版本
- **接口**: `POST /admin/rags/{versionId}`
- **认证**: 是(管理员)
- **描述**: 审核RAG版本

### 29.3 获取 RAG 版本详情
- **接口**: `GET /admin/rags/{versionId}`
- **认证**: 是(管理员)
- **描述**: 获取RAG版本详情(用于审核)

### 29.4 下架 RAG 版本
- **接口**: `POST /admin/rags/{versionId}/remove`
- **认证**: 是(管理员)
- **描述**: 下架RAG版本

### 29.5 获取 RAG 统计
- **接口**: `GET /admin/rags/statistics`
- **认证**: 是(管理员)
- **描述**: 获取RAG统计数据

### 29.6 获取所有 RAG 版本
- **接口**: `GET /admin/rags/versions`
- **认证**: 是(管理员)
- **描述**: 获取所有RAG版本列表(管理员用)

### 29.7 批量审核 RAG
- **接口**: `POST /admin/rags/batch-review`
- **认证**: 是(管理员)
- **描述**: 批量审核RAG版本

### 29.8 获取 RAG 内容预览
- **接口**: `GET /admin/rags/{versionId}/preview`
- **认证**: 是(管理员)
- **描述**: 获取RAG内容预览

---

## 30. 管理员-订单

### 30.1 分页获取所有订单
- **接口**: `GET /admin/orders`
- **认证**: 是(管理员)
- **描述**: 分页获取所有订单列表

### 30.2 获取订单详情
- **接口**: `GET /admin/orders/{orderId}`
- **认证**: 是(管理员)
- **描述**: 获取订单详情

---

## 31. 管理员-容器

### 31.1 分页获取容器列表
- **接口**: `GET /admin/containers`
- **认证**: 是(管理员)
- **描述**: 分页获取容器列表

### 31.2 获取容器统计
- **接口**: `GET /admin/containers/statistics`
- **认证**: 是(管理员)
- **描述**: 获取容器统计信息

### 31.3 删除容器
- **接口**: `DELETE /admin/containers/{containerId}`
- **认证**: 是(管理员)
- **描述**: 删除容器

### 31.4 获取容器日志
- **接口**: `GET /admin/containers/{containerId}/logs`
- **认证**: 是(管理员)
- **描述**: 获取容器日志
- **参数**:
  - `lines` (query): 获取日志行数

### 31.5 获取容器系统信息
- **接口**: `GET /admin/containers/{containerId}/system-info`
- **认证**: 是(管理员)
- **描述**: 获取容器系统信息

### 31.6 获取容器进程信息
- **接口**: `GET /admin/containers/{containerId}/processes`
- **认证**: 是(管理员)
- **描述**: 获取容器进程信息

### 31.7 获取容器网络信息
- **接口**: `GET /admin/containers/{containerId}/network`
- **认证**: 是(管理员)
- **描述**: 获取容器网络信息

### 31.8 检查 MCP 网关状态
- **接口**: `GET /admin/containers/{containerId}/mcp-status`
- **认证**: 是(管理员)
- **描述**: 检查容器内MCP网关状态

### 31.9 启动容器
- **接口**: `POST /admin/containers/{containerId}/start`
- **认证**: 是(管理员)
- **描述**: 启动容器

### 31.10 停止容器
- **接口**: `POST /admin/containers/{containerId}/stop`
- **认证**: 是(管理员)
- **描述**: 停止容器

### 31.11 获取或创建审核容器
- **接口**: `POST /admin/containers/review`
- **认证**: 是(管理员)
- **描述**: 获取或创建审核容器

### 31.12 检查审核容器健康状态
- **接口**: `GET /admin/containers/review/health`
- **认证**: 是(管理员)
- **描述**: 检查审核容器健康状态

### 31.13 重新创建审核容器
- **接口**: `POST /admin/containers/review/recreate`
- **认证**: 是(管理员)
- **描述**: 重新创建审核容器

### 31.14 从模板创建容器
- **接口**: `POST /admin/containers/from-template/{templateId}`
- **认证**: 是(管理员)
- **描述**: 从模板创建容器

---

## 32. 管理员-容器模板

### 32.1 分页获取模板列表
- **接口**: `GET /admin/container-templates`
- **认证**: 是(管理员)
- **描述**: 分页获取容器模板列表

### 32.2 获取模板详情
- **接口**: `GET /admin/container-templates/{templateId}`
- **认证**: 是(管理员)
- **描述**: 获取容器模板详情

### 32.3 根据类型获取模板
- **接口**: `GET /admin/container-templates/by-type/{type}`
- **认证**: 是(管理员)
- **描述**: 根据类型获取模板列表

### 32.4 获取默认模板
- **接口**: `GET /admin/container-templates/default/{type}`
- **认证**: 是(管理员)
- **描述**: 获取默认模板

### 32.5 获取所有启用的模板
- **接口**: `GET /admin/container-templates/enabled`
- **认证**: 是(管理员)
- **描述**: 获取所有启用的模板

### 32.6 创建容器模板
- **接口**: `POST /admin/container-templates`
- **认证**: 是(管理员)
- **描述**: 创建容器模板

### 32.7 更新容器模板
- **接口**: `PUT /admin/container-templates/{templateId}`
- **认证**: 是(管理员)
- **描述**: 更新容器模板

### 32.8 删除容器模板
- **接口**: `DELETE /admin/container-templates/{templateId}`
- **认证**: 是(管理员)
- **描述**: 删除容器模板

### 32.9 启用/禁用模板
- **接口**: `PUT /admin/container-templates/{templateId}/status`
- **认证**: 是(管理员)
- **描述**: 启用/禁用模板
- **参数**:
  - `enabled` (query): 是否启用

### 32.10 设置默认模板
- **接口**: `PUT /admin/container-templates/{templateId}/default`
- **认证**: 是(管理员)
- **描述**: 设置默认模板

### 32.11 获取模板统计
- **接口**: `GET /admin/container-templates/statistics`
- **认证**: 是(管理员)
- **描述**: 获取模板统计信息

---

## 33. 管理员-计费规则

### 33.1 创建计费规则
- **接口**: `POST /admin/rules`
- **认证**: 是(管理员)
- **描述**: 创建计费规则

### 33.2 更新计费规则
- **接口**: `PUT /admin/rules/{ruleId}`
- **认证**: 是(管理员)
- **描述**: 更新计费规则

### 33.3 根据ID获取规则
- **接口**: `GET /admin/rules/{ruleId}`
- **认证**: 是(管理员)
- **描述**: 根据ID获取计费规则

### 33.4 根据处理器标识获取规则
- **接口**: `GET /admin/rules/handler/{handlerKey}`
- **认证**: 是(管理员)
- **描述**: 根据处理器标识获取规则

### 33.5 分页查询计费规则
- **接口**: `GET /admin/rules`
- **认证**: 是(管理员)
- **描述**: 分页查询计费规则

### 33.6 获取所有计费规则
- **接口**: `GET /admin/rules/all`
- **认证**: 是(管理员)
- **描述**: 获取所有计费规则(不分页)

### 33.7 删除计费规则
- **接口**: `DELETE /admin/rules/{ruleId}`
- **认证**: 是(管理员)
- **描述**: 删除计费规则

### 33.8 检查规则是否存在
- **接口**: `GET /admin/rules/{ruleId}/exists`
- **认证**: 是(管理员)
- **描述**: 检查规则是否存在

### 33.9 检查处理器标识是否存在
- **接口**: `GET /admin/rules/handler/{handlerKey}/exists`
- **认证**: 是(管理员)
- **描述**: 检查处理器标识是否存在

---

## 34. 管理员-认证配置

### 34.1 获取所有认证配置
- **接口**: `GET /admin/auth-settings`
- **认证**: 是(管理员)
- **描述**: 获取所有认证配置

### 34.2 根据ID获取配置
- **接口**: `GET /admin/auth-settings/{id}`
- **认证**: 是(管理员)
- **描述**: 根据ID获取认证配置

### 34.3 切换配置状态
- **接口**: `PUT /admin/auth-settings/{id}/toggle`
- **认证**: 是(管理员)
- **描述**: 切换认证配置启用状态

### 34.4 更新认证配置
- **接口**: `PUT /admin/auth-settings/{id}`
- **认证**: 是(管理员)
- **描述**: 更新认证配置

### 34.5 删除认证配置
- **接口**: `DELETE /admin/auth-settings/{id}`
- **认证**: 是(管理员)
- **描述**: 删除认证配置

---

## 35. 外部 API

> 外部 API 使用 API Key 认证，需在 Header 中携带: `X-Api-Key: {your_api_key}`

### 35.1 发起对话
- **接口**: `POST /v1/chat/completions`
- **认证**: API Key
- **描述**: 发起对话，支持流式和同步响应
- **请求体**:
```json
{
  "message": "你好",
  "sessionId": "session123",
  "model": "gpt-4",
  "stream": true,
  "files": ["https://..."]
}
```
- **响应**: 根据 `stream` 参数返回流式或同步响应

### 35.2 获取可用模型列表
- **接口**: `GET /v1/models`
- **认证**: API Key
- **描述**: 获取可用模型列表

### 35.3 获取会话列表
- **接口**: `GET /v1/sessions`
- **认证**: API Key
- **描述**: 获取会话列表

### 35.4 创建新会话
- **接口**: `POST /v1/sessions`
- **认证**: API Key
- **描述**: 创建新会话
- **请求体**:
```json
{
  "title": "新会话"
}
```

### 35.5 删除会话
- **接口**: `DELETE /v1/sessions/{id}`
- **认证**: API Key
- **描述**: 删除会话

---

## 36. 公开小组件

> 公开小组件接口无需认证，但会验证域名访问权限

### 36.1 获取小组件信息
- **接口**: `GET /widget/{publicId}/info`
- **认证**: 否
- **描述**: 获取小组件配置信息(公开访问)

### 36.2 小组件聊天(流式)
- **接口**: `POST /widget/{publicId}/chat`
- **认证**: 否
- **描述**: 小组件聊天接口(流式)
- **响应类型**: `text/event-stream`

### 36.3 小组件聊天(同步)
- **接口**: `POST /widget/{publicId}/chat/sync`
- **认证**: 否
- **描述**: 小组件聊天接口(同步)

---

## 37. 容器模板(用户)

### 37.1 获取 MCP 网关模板
- **接口**: `GET /container-templates/mcp-gateway`
- **认证**: 是
- **描述**: 获取MCP网关默认模板

### 37.2 根据类型获取启用模板
- **接口**: `GET /container-templates/enabled/by-type/{type}`
- **认证**: 是
- **描述**: 根据类型获取启用的模板列表

### 37.3 获取所有启用模板
- **接口**: `GET /container-templates/enabled`
- **认证**: 是
- **描述**: 获取所有启用的模板

### 37.4 获取默认模板
- **接口**: `GET /container-templates/default/{type}`
- **认证**: 是
- **描述**: 获取默认模板

### 37.5 获取模板详情
- **接口**: `GET /container-templates/{templateId}`
- **认证**: 是
- **描述**: 获取模板详情

### 37.6 创建自定义模板
- **接口**: `POST /container-templates`
- **认证**: 是
- **描述**: 创建自定义模板(如果允许用户创建)

### 37.7 更新自定义模板
- **接口**: `PUT /container-templates/{templateId}`
- **认证**: 是
- **描述**: 更新自定义模板

### 37.8 删除自定义模板
- **接口**: `DELETE /container-templates/{templateId}`
- **认证**: 是
- **描述**: 删除自定义模板

---

## 附录

### 常见错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 参数错误 |
| 401 | 未认证 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 429 | 请求过于频繁 |
| 500 | 服务器内部错误 |

### 枚举值说明

#### Agent 版本状态
- `1` - 审核中
- `2` - 已发布
- `3` - 已拒绝
- `4` - 已下架

#### 工具状态
- `PENDING` - 待审核
- `APPROVED` - 审核通过
- `FAILED` - 审核失败
- `PUBLISHED` - 已发布

#### 模型类型
- `CHAT` - 对话模型
- `EMBEDDING` - 嵌入模型
- `IMAGE` - 图像模型

#### 服务商类型
- `official` - 官方
- `user` - 用户自定义

---

## 更新日志

- **2025-11-06**: 初始版本，基于 master 分支生成完整接口文档

---

**文档生成工具**: AgentX API Documentation Generator  
**项目地址**: https://github.com/lucky-aeon/AgentX

