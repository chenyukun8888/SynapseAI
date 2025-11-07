# AgentX 详细版本演进历史 - 第二阶段

## 第二阶段：会话管理与 Agent 功能 (2025-03-20 ~ 2025-03-22)

---

## v2.0 - AgentX-2025-03-20-feat-llm-chat-group

### 📅 版本信息
- **发布日期**: 2025-03-20
- **版本主题**: 会话分组与上下文管理
- **Java 文件数**: 40 (+24)
- **前端文件**: 97 TypeScript/TSX 文件
- **数据库表**: 3 个（首次引入）
- **开发周期**: 1天
- **重大变更**: 引入数据库持久化，实现会话管理，前端架构升级到 Next.js

### 🎯 版本目标
1. 实现会话（Session）和消息（Message）的持久化存储
2. 支持多会话管理和切换
3. 维护对话上下文
4. 前端从 Vue.js 迁移到 Next.js + TypeScript
5. 提供更好的用户对话体验

### 📦 完整项目架构

```
AgentX-2025-03-20-feat-llm-chat-group/
├── AgentX/                                          # 后端项目（Spring Boot）
│   ├── src/main/java/org/xhy/
│   │   ├── AgentXApplication.java                   # Spring Boot 启动类
│   │   │
│   │   ├── interfaces/                              # 📦 接口层
│   │   │   ├── api/
│   │   │   │   ├── base/
│   │   │   │   │   └── HealthController.java
│   │   │   │   ├── common/
│   │   │   │   │   └── Result.java
│   │   │   │   └── conversation/
│   │   │   │       └── ConversationController.java  # 对话控制器
│   │   │   └── dto/
│   │   │       └── conversation/
│   │   │           ├── CreateSessionRequest.java    # 🆕 创建会话请求
│   │   │           ├── UpdateSessionRequest.java    # 🆕 更新会话请求
│   │   │           └── SendMessageRequest.java      # 🆕 发送消息请求
│   │   │
│   │   ├── application/                             # 📦 应用层
│   │   │   └── conversation/
│   │   │       ├── dto/
│   │   │       │   ├── ChatRequest.java
│   │   │       │   ├── ChatResponse.java
│   │   │       │   ├── SessionDTO.java              # 🆕 会话DTO
│   │   │       │   ├── MessageDTO.java              # 🆕 消息DTO
│   │   │       │   └── ContextDTO.java              # 🆕 上下文DTO
│   │   │       └── service/
│   │   │           ├── ConversationService.java     # 🔄 重构（支持会话）
│   │   │           ├── SessionAppService.java       # 🆕 会话应用服务
│   │   │           └── MessageAppService.java       # 🆕 消息应用服务
│   │   │
│   │   ├── domain/                                  # 📦 领域层
│   │   │   ├── llm/                                # LLM 领域（已存在）
│   │   │   │   ├── model/
│   │   │   │   │   ├── LlmMessage.java
│   │   │   │   │   ├── LlmRequest.java
│   │   │   │   │   └── LlmResponse.java
│   │   │   │   └── service/
│   │   │   │       └── LlmService.java
│   │   │   │
│   │   │   └── conversation/                        # 🆕 对话领域
│   │   │       ├── model/
│   │   │       │   ├── Session.java                 # 🆕 会话聚合根
│   │   │       │   ├── SessionDTO.java              # 🆕
│   │   │       │   ├── Message.java                 # 🆕 消息实体
│   │   │       │   ├── MessageDTO.java              # 🆕
│   │   │       │   └── Context.java                 # 🆕 上下文实体
│   │   │       ├── repository/                      # 🆕 仓储接口
│   │   │       │   ├── SessionRepository.java
│   │   │       │   ├── MessageRepository.java
│   │   │       │   └── ContextRepository.java
│   │   │       └── service/                         # 🆕 领域服务
│   │   │           ├── SessionService.java
│   │   │           ├── MessageService.java
│   │   │           ├── ContextService.java
│   │   │           └── impl/
│   │   │               ├── SessionServiceImpl.java
│   │   │               ├── MessageServiceImpl.java
│   │   │               └── ContextServiceImpl.java
│   │   │
│   │   └── infrastructure/                          # 📦 基础设施层
│   │       ├── config/
│   │       │   ├── LlmConfig.java
│   │       │   ├── MyBatisPlusConfig.java           # 🆕 MyBatis-Plus 配置
│   │       │   └── WebConfig.java                   # 🆕 Web 配置
│   │       ├── integration/
│   │       │   └── llm/
│   │       │       ├── AbstractLlmService.java
│   │       │       └── siliconflow/
│   │       │           └── SiliconFlowLlmService.java
│   │       ├── persistence/                         # 🆕 持久化层
│   │       │   ├── po/
│   │       │   │   ├── SessionPO.java               # 会话PO
│   │       │   │   ├── MessagePO.java               # 消息PO
│   │       │   │   └── ContextPO.java               # 上下文PO
│   │       │   └── mapper/
│   │       │       ├── SessionMapper.java           # MyBatis-Plus Mapper
│   │       │       ├── MessageMapper.java
│   │       │       └── ContextMapper.java
│   │       ├── repository/                          # 🆕 仓储实现
│   │       │   ├── SessionRepositoryImpl.java
│   │       │   ├── MessageRepositoryImpl.java
│   │       │   └── ContextRepositoryImpl.java
│   │       ├── typehandler/                         # 🆕 类型处理器
│   │       │   └── JsonTypeHandler.java             # JSONB 类型处理
│   │       └── utils/
│   │           └── JsonUtils.java                   # 🆕 JSON 工具类
│   │
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── mapper/                                  # 🆕 MyBatis XML
│           ├── SessionMapper.xml
│           ├── MessageMapper.xml
│           └── ContextMapper.xml
│
├── agentx-frontend-plus/                            # 🆕 Next.js 前端（全新）
│   ├── src/
│   │   ├── app/                                     # App Router
│   │   │   ├── page.tsx                            # 首页
│   │   │   ├── layout.tsx                          # 布局
│   │   │   ├── chat/
│   │   │   │   └── page.tsx                        # 聊天页面
│   │   │   └── sessions/
│   │   │       └── page.tsx                        # 会话列表页
│   │   ├── components/
│   │   │   ├── ui/                                 # Shadcn/ui 组件
│   │   │   │   ├── button.tsx
│   │   │   │   ├── input.tsx
│   │   │   │   ├── card.tsx
│   │   │   │   ├── dialog.tsx
│   │   │   │   └── ...
│   │   │   ├── ChatBox.tsx                         # 聊天框组件
│   │   │   ├── SessionList.tsx                     # 会话列表组件
│   │   │   ├── MessageItem.tsx                     # 消息项组件
│   │   │   ├── MessageInput.tsx                    # 消息输入组件
│   │   │   └── Header.tsx                          # 头部组件
│   │   ├── lib/
│   │   │   ├── api.ts                              # API 客户端
│   │   │   ├── types.ts                            # TypeScript 类型定义
│   │   │   └── utils.ts                            # 工具函数
│   │   └── hooks/
│   │       ├── useSession.ts                       # 会话Hook
│   │       ├── useMessages.ts                      # 消息Hook
│   │       └── useChat.ts                          # 聊天Hook
│   ├── public/                                      # 静态资源
│   ├── package.json
│   ├── tsconfig.json
│   ├── tailwind.config.ts                           # Tailwind 配置
│   ├── next.config.js
│   └── postcss.config.js
│
├── docs/
│   ├── README.md
│   └── sql/
│       └── init.sql                                 # 🆕 数据库初始化脚本
│
├── script/
│   ├── start.sh                                     # 🆕 启动脚本
│   ├── stop.sh                                      # 🆕 停止脚本
│   └── docker-compose.yml                           # 🔄 更新 Docker 配置
│
├── pom.xml
└── README.md
```

### 💾 数据库设计（全新）

**docs/sql/init.sql** - 完整数据库初始化脚本

```sql
-- ============================
-- AgentX 数据库初始化脚本
-- 版本: v2.0
-- 日期: 2025-03-20
-- ============================

-- 会话表
CREATE TABLE sessions (
    id VARCHAR(36) PRIMARY KEY COMMENT '会话ID',
    title VARCHAR(255) NOT NULL COMMENT '会话标题',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    description TEXT COMMENT '会话描述',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL COMMENT '更新时间',
    is_archived BOOLEAN DEFAULT FALSE COMMENT '是否归档',
    metadata JSONB COMMENT '元数据(JSON格式)'
);

-- 消息表
CREATE TABLE messages (
    id VARCHAR(36) PRIMARY KEY COMMENT '消息ID',
    session_id VARCHAR(36) NOT NULL COMMENT '所属会话ID',
    role VARCHAR(20) NOT NULL COMMENT '角色: user/assistant/system',
    content TEXT NOT NULL COMMENT '消息内容',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    token_count INTEGER COMMENT 'Token使用数量',
    provider VARCHAR(50) COMMENT 'LLM服务商',
    model VARCHAR(50) COMMENT '使用的模型',
    metadata JSONB COMMENT '元数据(JSON格式)'
);

-- 上下文表
CREATE TABLE context (
    id VARCHAR(36) PRIMARY KEY COMMENT '上下文ID',
    session_id VARCHAR(36) NOT NULL COMMENT '所属会话ID',
    active_messages JSONB COMMENT '活跃消息ID列表(JSON数组)',
    summary TEXT COMMENT '历史消息摘要',
    updated_at TIMESTAMP NOT NULL COMMENT '更新时间'
);

-- ============================
-- 创建索引
-- ============================

-- sessions 表索引
CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_created_at ON sessions(created_at);
CREATE INDEX idx_sessions_updated_at ON sessions(updated_at);
CREATE INDEX idx_sessions_is_archived ON sessions(is_archived);

-- messages 表索引
CREATE INDEX idx_messages_session_id ON messages(session_id);
CREATE INDEX idx_messages_created_at ON messages(created_at);
CREATE INDEX idx_messages_role ON messages(role);

-- context 表索引
CREATE INDEX idx_context_session_id ON context(session_id);

-- ============================
-- 表注释（PostgreSQL）
-- ============================

COMMENT ON TABLE sessions IS '会话表,存储用户的对话会话信息';
COMMENT ON TABLE messages IS '消息表,存储会话中的所有消息记录';
COMMENT ON TABLE context IS '上下文表,管理对话上下文和历史摘要';
```

**表结构详细说明**:

#### 1. sessions 表（会话表）

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| id | VARCHAR(36) | 主键，UUID | `a1b2c3d4-e5f6-...` |
| title | VARCHAR(255) | 会话标题 | `关于 AI 的讨论` |
| user_id | VARCHAR(36) | 用户ID | `user_123` |
| description | TEXT | 会话描述（可选） | `讨论人工智能的发展` |
| created_at | TIMESTAMP | 创建时间 | `2025-03-20 10:00:00` |
| updated_at | TIMESTAMP | 最后更新时间 | `2025-03-20 15:30:00` |
| is_archived | BOOLEAN | 是否已归档 | `false` |
| metadata | JSONB | 扩展元数据 | `{"tags":["AI","技术"]}` |

**业务规则**:
- 每个用户可以创建多个会话
- 会话可以被归档，但不物理删除
- metadata 字段存储灵活的扩展信息

#### 2. messages 表（消息表）

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| id | VARCHAR(36) | 主键，UUID | `m1a2b3c4-...` |
| session_id | VARCHAR(36) | 所属会话ID | `a1b2c3d4-...` |
| role | VARCHAR(20) | 消息角色 | `user`, `assistant`, `system` |
| content | TEXT | 消息内容 | `你好，请介绍一下AI` |
| created_at | TIMESTAMP | 创建时间 | `2025-03-20 10:05:00` |
| token_count | INTEGER | Token 使用量 | `45` |
| provider | VARCHAR(50) | LLM 服务商 | `siliconflow` |
| model | VARCHAR(50) | 使用的模型 | `Qwen/Qwen2.5-7B-Instruct` |
| metadata | JSONB | 扩展元数据 | `{"temperature":0.7}` |

**业务规则**:
- 每条消息必须归属于一个会话
- role 字段标识消息来源：
  - `user`: 用户发送的消息
  - `assistant`: AI 助手的回复
  - `system`: 系统提示消息
- token_count 用于统计和计费

#### 3. context 表（上下文表）

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| id | VARCHAR(36) | 主键，UUID | `c1a2b3c4-...` |
| session_id | VARCHAR(36) | 所属会话ID | `a1b2c3d4-...` |
| active_messages | JSONB | 活跃消息ID数组 | `["m1","m2","m3"]` |
| summary | TEXT | 历史消息摘要 | `用户询问了AI的定义...` |
| updated_at | TIMESTAMP | 更新时间 | `2025-03-20 15:30:00` |

**业务规则**:
- 每个会话对应一个上下文
- active_messages 保存当前对话窗口的消息ID
- summary 用于摘要过长的历史对话（为后续 Token 优化预留）

### 🆕 新增核心代码

#### 1. 领域模型 - Session（会话聚合根）

```java
package org.xhy.domain.conversation.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 会话聚合根
 * 
 * 聚合根职责：
 * - 维护会话的完整生命周期
 * - 保证会话数据的一致性
 * - 提供业务行为方法
 * 
 * @author AgentX Team
 * @since 2025-03-20
 */
public class Session {
    
    /** 会话ID - 聚合根标识 */
    private String id;
    
    /** 会话标题 */
    private String title;
    
    /** 用户ID */
    private String userId;
    
    /** 会话描述 */
    private String description;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 是否已归档 */
    private Boolean isArchived;
    
    /** 元数据 */
    private Map<String, Object> metadata;
    
    /**
     * 私有构造函数 - 防止直接实例化
     * 必须通过工厂方法创建
     */
    private Session() {
        this.metadata = new HashMap<>();
    }
    
    /**
     * 创建新会话（工厂方法）
     * 
     * @param title 会话标题
     * @param userId 用户ID
     * @return 新创建的会话实例
     */
    public static Session create(String title, String userId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("会话标题不能为空");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        Session session = new Session();
        session.setId(UUID.randomUUID().toString());
        session.setTitle(title);
        session.setUserId(userId);
        session.setCreatedAt(LocalDateTime.now());
        session.setUpdatedAt(LocalDateTime.now());
        session.setIsArchived(false);
        
        return session;
    }
    
    /**
     * 归档会话（领域行为）
     * 
     * 业务规则：
     * - 已归档的会话不能再接收新消息
     * - 归档操作会更新 updated_at 时间戳
     */
    public void archive() {
        this.isArchived = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 恢复归档的会话（领域行为）
     */
    public void unarchive() {
        this.isArchived = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新会话标题（领域行为）
     * 
     * @param newTitle 新标题
     */
    public void updateTitle(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (newTitle.length() > 255) {
            throw new IllegalArgumentException("标题长度不能超过255个字符");
        }
        this.title = newTitle;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新会话描述（领域行为）
     * 
     * @param description 新描述
     */
    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加元数据（领域行为）
     * 
     * @param key 键
     * @param value 值
     */
    public void addMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new HashMap<>();
        }
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查会话是否可以接收新消息
     * 
     * @return true表示可以接收，false表示不可以
     */
    public boolean canReceiveMessage() {
        return !this.isArchived;
    }
    
    // ======== Getters and Setters ========
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getIsArchived() {
        return isArchived;
    }
    
    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
```

#### 2. 领域模型 - Message（消息实体）

```java
package org.xhy.domain.conversation.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消息实体
 * 
 * @author AgentX Team
 * @since 2025-03-20
 */
public class Message {
    
    private String id;
    private String sessionId;
    private String role;  // user, assistant, system
    private String content;
    private Integer tokenCount;
    private String provider;
    private String model;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;
    
    /**
     * 创建用户消息（工厂方法）
     */
    public static Message createUserMessage(String sessionId, String content) {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSessionId(sessionId);
        message.setRole("user");
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setMetadata(new HashMap<>());
        return message;
    }
    
    /**
     * 创建助手消息（工厂方法）
     */
    public static Message createAssistantMessage(
            String sessionId, 
            String content, 
            String provider,
            String model,
            Integer tokenCount) {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSessionId(sessionId);
        message.setRole("assistant");
        message.setContent(content);
        message.setProvider(provider);
        message.setModel(model);
        message.setTokenCount(tokenCount);
        message.setCreatedAt(LocalDateTime.now());
        message.setMetadata(new HashMap<>());
        return message;
    }
    
    /**
     * 创建系统消息（工厂方法）
     */
    public static Message createSystemMessage(String sessionId, String content) {
        Message message = new Message();
        message.setId(UUID.randomUUID().toString());
        message.setSessionId(sessionId);
        message.setRole("system");
        message.setContent(content);
        message.setCreatedAt(LocalDateTime.now());
        message.setMetadata(new HashMap<>());
        return message;
    }
    
    // Getters and Setters...
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Integer getTokenCount() {
        return tokenCount;
    }
    
    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
```

#### 3. 仓储接口 - SessionRepository

```java
package org.xhy.domain.conversation.repository;

import org.xhy.domain.conversation.model.Session;
import java.util.List;
import java.util.Optional;

/**
 * 会话仓储接口
 * 
 * 定义会话数据访问的契约，具体实现在基础设施层
 * 
 * @author AgentX Team
 * @since 2025-03-20
 */
public interface SessionRepository {
    
    /**
     * 保存会话
     * 
     * @param session 会话实体
     */
    void save(Session session);
    
    /**
     * 根据ID查询会话
     * 
     * @param id 会话ID
     * @return Optional包装的会话实体
     */
    Optional<Session> findById(String id);
    
    /**
     * 查询用户的所有会话
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    List<Session> findByUserId(String userId);
    
    /**
     * 查询用户的活跃会话（未归档）
     * 
     * @param userId 用户ID
     * @return 活跃会话列表
     */
    List<Session> findActiveByUserId(String userId);
    
    /**
     * 查询用户的归档会话
     * 
     * @param userId 用户ID
     * @return 归档会话列表
     */
    List<Session> findArchivedByUserId(String userId);
    
    /**
     * 更新会话
     * 
     * @param session 会话实体
     */
    void update(Session session);
    
    /**
     * 删除会话
     * 
     * @param id 会话ID
     */
    void delete(String id);
    
    /**
     * 统计用户的会话数量
     * 
     * @param userId 用户ID
     * @return 会话数量
     */
    int countByUserId(String userId);
}
```

#### 4. 持久化对象 - SessionPO

```java
package org.xhy.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 会话持久化对象
 * 
 * 映射数据库表 sessions
 * 
 * @author AgentX Team
 * @since 2025-03-20
 */
@TableName("sessions")
public class SessionPO {
    
    /** 主键ID */
    @TableId(type = IdType.INPUT)
    private String id;
    
    /** 会话标题 */
    private String title;
    
    /** 用户ID */
    @TableField("user_id")
    private String userId;
    
    /** 会话描述 */
    private String description;
    
    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    /** 是否归档 */
    @TableField("is_archived")
    private Boolean isArchived;
    
    /** 元数据（JSONB） */
    @TableField(value = "metadata", typeHandler = org.xhy.infrastructure.typehandler.JsonTypeHandler.class)
    private String metadata;  // 存储 JSON 字符串
    
    // 完整的 Getters and Setters...
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Boolean getIsArchived() {
        return isArchived;
    }
    
    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
```

#### 5. MyBatis-Plus Mapper

```java
package org.xhy.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.xhy.infrastructure.persistence.po.SessionPO;

/**
 * 会话 Mapper
 * 
 * 继承 MyBatis-Plus 的 BaseMapper，获得基础 CRUD 能力
 * 
 * @author AgentX Team
 * @since 2025-03-20
 */
@Mapper
public interface SessionMapper extends BaseMapper<SessionPO> {
    // MyBatis-Plus 提供的基础方法：
    // - insert(T entity)
    // - deleteById(Serializable id)
    // - updateById(T entity)
    // - selectById(Serializable id)
    // - selectList(Wrapper<T> queryWrapper)
    // - selectCount(Wrapper<T> queryWrapper)
    // 等等...
    
    // 自定义复杂查询可以在这里添加方法，并在 XML 中实现
}
```

#### 6. 仓储实现 - SessionRepositoryImpl

```java
package org.xhy.infrastructure.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.xhy.domain.conversation.model.Session;
import org.xhy.domain.conversation.repository.SessionRepository;
import org.xhy.infrastructure.persistence.mapper.SessionMapper;
import org.xhy.infrastructure.persistence.po.SessionPO;
import org.xhy.infrastructure.utils.JsonUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 会话仓储实现
 * 
 * 使用 MyBatis-Plus 实现数据持久化
 * 
 * @author AgentX Team
 * @since 2025-03-20
 */
@Repository
public class SessionRepositoryImpl implements SessionRepository {
    
    @Autowired
    private SessionMapper sessionMapper;
    
    @Override
    public void save(Session session) {
        SessionPO po = convertToPO(session);
        sessionMapper.insert(po);
    }
    
    @Override
    public Optional<Session> findById(String id) {
        SessionPO po = sessionMapper.selectById(id);
        return Optional.ofNullable(po).map(this::convertToEntity);
    }
    
    @Override
    public List<Session> findByUserId(String userId) {
        QueryWrapper<SessionPO> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .orderByDesc("updated_at");
        
        List<SessionPO> poList = sessionMapper.selectList(wrapper);
        return poList.stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
    }
    
    @Override
    public List<Session> findActiveByUserId(String userId) {
        QueryWrapper<SessionPO> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("is_archived", false)
               .orderByDesc("updated_at");
        
        List<SessionPO> poList = sessionMapper.selectList(wrapper);
        return poList.stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
    }
    
    @Override
    public List<Session> findArchivedByUserId(String userId) {
        QueryWrapper<SessionPO> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
               .eq("is_archived", true)
               .orderByDesc("updated_at");
        
        List<SessionPO> poList = sessionMapper.selectList(wrapper);
        return poList.stream()
                    .map(this::convertToEntity)
                    .collect(Collectors.toList());
    }
    
    @Override
    public void update(Session session) {
        SessionPO po = convertToPO(session);
        sessionMapper.updateById(po);
    }
    
    @Override
    public void delete(String id) {
        sessionMapper.deleteById(id);
    }
    
    @Override
    public int countByUserId(String userId) {
        QueryWrapper<SessionPO> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return sessionMapper.selectCount(wrapper).intValue();
    }
    
    /**
     * 实体转PO
     */
    private SessionPO convertToPO(Session session) {
        SessionPO po = new SessionPO();
        po.setId(session.getId());
        po.setTitle(session.getTitle());
        po.setUserId(session.getUserId());
        po.setDescription(session.getDescription());
        po.setCreatedAt(session.getCreatedAt());
        po.setUpdatedAt(session.getUpdatedAt());
        po.setIsArchived(session.getIsArchived());
        
        // 将 Map 转换为 JSON 字符串
        if (session.getMetadata() != null) {
            po.setMetadata(JsonUtils.toJson(session.getMetadata()));
        }
        
        return po;
    }
    
    /**
     * PO转实体
     */
    private Session convertToEntity(SessionPO po) {
        Session session = new Session();
        session.setId(po.getId());
        session.setTitle(po.getTitle());
        session.setUserId(po.getUserId());
        session.setDescription(po.getDescription());
        session.setCreatedAt(po.getCreatedAt());
        session.setUpdatedAt(po.getUpdatedAt());
        session.setIsArchived(po.getIsArchived());
        
        // 将 JSON 字符串转换为 Map
        if (po.getMetadata() != null) {
            session.setMetadata(JsonUtils.fromJson(po.getMetadata(), Map.class));
        }
        
        return session;
    }
}
```

#### 7. JSON 类型处理器 - JsonTypeHandler

```java
package org.xhy.infrastructure.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * PostgreSQL JSONB 类型处理器
 * 
 * 用于处理 PostgreSQL 的 JSONB 类型字段
 * 
 * @author AgentX Team
 * @since 2025-03-20
 */
@MappedTypes({String.class})
public class JsonTypeHandler extends BaseTypeHandler<String> {
    
    /**
     * 设置非空参数
     * 将 String 转换为 PGobject 存入数据库
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, 
                                     String parameter, JdbcType jdbcType) 
            throws SQLException {
        PGobject jsonObject = new PGobject();
        jsonObject.setType("jsonb");
        jsonObject.setValue(parameter);
        ps.setObject(i, jsonObject);
    }
    
    /**
     * 根据列名获取可为空的结果
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) 
            throws SQLException {
        return rs.getString(columnName);
    }
    
    /**
     * 根据列索引获取可为空的结果
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) 
            throws SQLException {
        return rs.getString(columnIndex);
    }
    
    /**
     * 获取存储过程的可为空结果
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) 
            throws SQLException {
        return cs.getString(columnIndex);
    }
}
```

#### 8. MyBatis-Plus 配置

```java
package org.xhy.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 * 
 * @author AgentX Team
 * @since 2025-03-20
 */
@Configuration
@MapperScan("org.xhy.infrastructure.persistence.mapper")
public class MyBatisPlusConfig {
    
    /**
     * 配置 MyBatis-Plus 拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加分页插件
        PaginationInnerInterceptor paginationInterceptor = 
            new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        paginationInterceptor.setMaxLimit(1000L);  // 最大单页限制
        
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        return interceptor;
    }
}
```

---

## v2.1 - AgentX-2025-03-21-feat-agent

### 📅 版本信息
- **发布日期**: 2025-03-21
- **版本主题**: Agent 管理功能
- **Java 文件数**: 75 (+35)
- **前端文件**: 128 TypeScript/TSX 文件 (+31)
- **数据库表**: 6 个（+3）
- **开发周期**: 1天
- **重大变更**: 引入 Agent 核心功能，实现 Agent 的创建、配置、版本管理

### 🎯 版本目标
1. 实现 Agent 数据模型和领域服务
2. 支持 Agent 的创建、编辑、删除、查询
3. 实现 Agent 版本管理和发布机制
4. Agent 与会话关联
5. Agent 配置管理（系统提示词、工具、知识库等）
6. 前端实现 Agent 管理界面

### 📦 完整项目架构

```
AgentX-2025-03-21-feat-agent/
├── AgentX/
│   └── src/main/java/org/xhy/
│       ├── domain/
│       │   ├── agent/                          # 🆕 Agent 领域
│       │   │   ├── model/
│       │   │   │   ├── AgentEntity.java        # 🆕 Agent 聚合根
│       │   │   │   ├── AgentDTO.java           # 🆕
│       │   │   │   ├── AgentVersionEntity.java # 🆕 版本实体
│       │   │   │   ├── AgentVersionDTO.java    # 🆕
│       │   │   │   ├── AgentType.java          # 🆕 Agent类型枚举
│       │   │   │   ├── AgentStatus.java        # 🆕 Agent状态枚举
│       │   │   │   ├── PublishStatus.java      # 🆕 发布状态枚举
│       │   │   │   ├── AgentTool.java          # 🆕 工具配置
│       │   │   │   └── ModelConfig.java        # 🆕 模型配置
│       │   │   ├── repository/
│       │   │   │   ├── AgentRepository.java    # 🆕
│       │   │   │   └── AgentVersionRepository.java # 🆕
│       │   │   └── service/
│       │   │       ├── AgentService.java       # 🆕 Agent领域服务
│       │   │       └── impl/
│       │   │           └── AgentServiceImpl.java # 🆕
│       │   └── conversation/                   # 已存在
│       │       └── model/
│       │           └── Session.java            # 🔄 新增 agentId 字段
│       │
│       ├── application/
│       │   └── agent/                          # 🆕 Agent 应用层
│       │       ├── dto/
│       │       │   ├── AgentDTO.java
│       │       │   └── AgentVersionDTO.java
│       │       ├── assembler/
│       │       │   └── AgentAssembler.java     # 🆕 DTO转换器
│       │       └── service/
│       │           └── AgentAppService.java    # 🆕 Agent应用服务
│       │
│       ├── interfaces/
│       │   ├── api/
│       │   │   ├── admin/                      # 🆕 管理员接口
│       │   │   │   └── AdminAgentController.java # 🆕
│       │   │   └── portal/                     # 🆕 门户接口
│       │   │       └── agent/
│       │   │           └── PortalAgentController.java # 🆕
│       │   └── dto/
│       │       └── agent/                      # 🆕 Agent 请求/响应 DTO
│       │           ├── CreateAgentRequest.java
│       │           ├── UpdateAgentRequest.java
│       │           ├── UpdateAgentBasicInfoRequest.java
│       │           ├── UpdateAgentConfigRequest.java
│       │           ├── PublishAgentVersionRequest.java
│       │           ├── ReviewAgentVersionRequest.java
│       │           ├── SearchAgentsRequest.java
│       │           └── UpdateAgentStatusRequest.java
│       │
│       └── infrastructure/
│           ├── persistence/
│           │   ├── po/
│           │   │   ├── AgentPO.java            # 🆕
│           │   │   ├── AgentVersionPO.java     # 🆕
│           │   │   └── AgentWorkspacePO.java   # 🆕
│           │   └── mapper/
│           │       ├── AgentMapper.java        # 🆕
│           │       ├── AgentVersionMapper.java # 🆕
│           │       └── AgentWorkspaceMapper.java # 🆕
│           └── repository/
│               ├── AgentRepositoryImpl.java    # 🆕
│               └── AgentVersionRepositoryImpl.java # 🆕
│
├── agentx-frontend-plus/
│   └── src/
│       ├── app/
│       │   ├── agents/                         # 🆕 Agent 管理页面
│       │   │   ├── page.tsx                    # Agent 列表
│       │   │   ├── create/
│       │   │   │   └── page.tsx                # 创建 Agent
│       │   │   └── [id]/
│       │   │       ├── page.tsx                # Agent 详情
│       │   │       ├── edit/
│       │   │       │   └── page.tsx            # 编辑 Agent
│       │   │       └── config/
│       │   │           └── page.tsx            # Agent 配置
│       │   └── admin/
│       │       └── agents/                     # 🆕 管理员 Agent 审核
│       │           └── page.tsx
│       └── components/
│           ├── agent/                          # 🆕 Agent 相关组件
│           │   ├── AgentCard.tsx               # Agent 卡片
│           │   ├── AgentList.tsx               # Agent 列表
│           │   ├── AgentForm.tsx               # Agent 表单
│           │   ├── AgentConfigPanel.tsx        # 配置面板
│           │   └── AgentVersionList.tsx        # 版本列表
│           └── ui/
│               └── ...                         # Shadcn/ui 组件
│
└── docs/
    └── sql/
        └── init.sql                            # 🔄 更新：新增 Agent 相关表
```

### 💾 数据库设计（新增）

**新增表结构**:

```sql
-- ============================
-- Agent 相关表结构
-- 版本: v2.1
-- 日期: 2025-03-21
-- ============================

-- 1. Agent表：核心表，存储Agent实体信息和当前编辑中的配置
CREATE TABLE agents (
    id VARCHAR(36) PRIMARY KEY COMMENT 'Agent ID',
    name VARCHAR(50) NOT NULL COMMENT 'Agent 名称',
    avatar VARCHAR(255) COMMENT 'Agent 头像URL',
    description TEXT COMMENT 'Agent 描述',
    
    -- 当前编辑中的配置（未发布）
    system_prompt TEXT COMMENT 'Agent 系统提示词',
    welcome_message TEXT COMMENT '欢迎消息',
    model_config JSONB COMMENT '模型配置（温度、最大token等）',
    tools JSONB COMMENT 'Agent 可使用的工具列表',
    knowledge_base_ids JSONB COMMENT '关联的知识库ID列表',
    
    -- 版本管理
    published_version VARCHAR(36) COMMENT '当前发布的版本ID',
    
    -- 状态管理
    enabled BOOLEAN DEFAULT TRUE COMMENT 'Agent 是否启用',
    agent_type SMALLINT DEFAULT 1 COMMENT 'Agent类型：1-聊天助手, 2-功能性Agent',
    
    -- 审计字段
    user_id VARCHAR(36) NOT NULL COMMENT '创建者用户ID',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL COMMENT '更新时间',
    deleted_at TIMESTAMP COMMENT '软删除标记'
);

-- 2. Agent版本表：存储Agent的已发布版本
CREATE TABLE agent_versions (
    id VARCHAR(36) PRIMARY KEY COMMENT '版本ID',
    agent_id VARCHAR(36) NOT NULL COMMENT '关联的Agent ID',
    
    -- 版本快照信息（发布时的配置）
    name VARCHAR(50) NOT NULL COMMENT 'Agent 名称（快照）',
    avatar VARCHAR(255) COMMENT 'Agent 头像URL（快照）',
    description TEXT COMMENT 'Agent 描述（快照）',
    version_number VARCHAR(20) NOT NULL COMMENT '版本号，如 1.0.0',
    
    -- 版本配置快照
    system_prompt TEXT COMMENT 'Agent 系统提示词',
    welcome_message TEXT COMMENT '欢迎消息',
    model_config JSONB COMMENT '模型配置',
    tools JSONB COMMENT '工具列表',
    knowledge_base_ids JSONB COMMENT '知识库ID列表',
    
    -- 版本元信息
    agent_type SMALLINT DEFAULT 1 COMMENT 'Agent类型',
    change_log TEXT COMMENT '版本更新日志',
    
    -- 发布状态管理
    publish_status SMALLINT DEFAULT 1 COMMENT '发布状态：1-审核中, 2-已发布, 3-拒绝, 4-已下架',
    reject_reason TEXT COMMENT '审核拒绝原因',
    review_time TIMESTAMP COMMENT '审核时间',
    published_at TIMESTAMP COMMENT '发布时间',
    
    -- 审计字段
    user_id VARCHAR(36) NOT NULL COMMENT '发布者用户ID',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL COMMENT '更新时间',
    deleted_at TIMESTAMP COMMENT '软删除标记'
);

-- 3. Agent工作区表：用于记录用户添加到工作区的Agent
CREATE TABLE agent_workspace (
    id VARCHAR(36) PRIMARY KEY COMMENT '工作区ID',
    agent_id VARCHAR(36) NOT NULL COMMENT '添加到工作区的Agent ID',
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    created_at TIMESTAMP NOT NULL COMMENT '添加时间',
    
    UNIQUE (agent_id, user_id) COMMENT '防止重复添加'
);

-- 4. 更新sessions表，新增agent_id字段
ALTER TABLE sessions ADD COLUMN agent_id VARCHAR(36) COMMENT '关联的Agent ID';

-- ============================
-- 创建索引
-- ============================

-- Agent 表索引
CREATE INDEX idx_agents_user_id ON agents(user_id);
CREATE INDEX idx_agents_enabled ON agents(enabled);
CREATE INDEX idx_agents_agent_type ON agents(agent_type);
CREATE INDEX idx_agents_name ON agents(name);  -- 支持按名称搜索
CREATE INDEX idx_agents_deleted_at ON agents(deleted_at);  -- 支持软删除过滤

-- Agent 版本表索引
CREATE INDEX idx_agent_versions_agent_id ON agent_versions(agent_id);
CREATE INDEX idx_agent_versions_published_at ON agent_versions(published_at);
CREATE INDEX idx_agent_versions_publish_status ON agent_versions(publish_status);
CREATE INDEX idx_agent_versions_deleted_at ON agent_versions(deleted_at);

-- Agent 工作区表索引
CREATE INDEX idx_agent_workspace_user_id ON agent_workspace(user_id);
CREATE INDEX idx_agent_workspace_agent_id ON agent_workspace(agent_id);

-- Sessions 表新增索引
CREATE INDEX idx_sessions_agent_id ON sessions(agent_id);

-- ============================
-- 表注释
-- ============================

COMMENT ON TABLE agents IS 'Agent表，存储AI助手的基本信息和配置';
COMMENT ON TABLE agent_versions IS 'Agent版本表，记录Agent的各个版本';
COMMENT ON TABLE agent_workspace IS 'Agent工作区表，记录用户添加到工作区的Agent';
```

**详细字段说明**:

#### 1. agents 表（Agent主表）

| 字段 | 类型 | 说明 | 业务规则 |
|------|------|------|----------|
| id | VARCHAR(36) | 主键 | UUID格式 |
| name | VARCHAR(50) | Agent名称 | 必填，最长50字符 |
| avatar | VARCHAR(255) | 头像URL | 可选 |
| description | TEXT | Agent描述 | 详细介绍Agent的用途 |
| system_prompt | TEXT | 系统提示词 | 定义Agent的行为和性格 |
| welcome_message | TEXT | 欢迎消息 | 用户首次对话时显示 |
| model_config | JSONB | 模型配置 | `{"model":"gpt-4","temperature":0.7,"maxTokens":2048}` |
| tools | JSONB | 工具列表 | `[{"id":"calc","name":"计算器"}]` |
| knowledge_base_ids | JSONB | 知识库ID列表 | `["kb_1","kb_2"]` |
| published_version | VARCHAR(36) | 当前发布版本ID | 关联 agent_versions 表 |
| enabled | BOOLEAN | 是否启用 | false-禁用，true-启用 |
| agent_type | SMALLINT | Agent类型 | 1-聊天助手，2-功能性Agent |
| user_id | VARCHAR(36) | 创建者ID | 标识谁创建的Agent |
| created_at | TIMESTAMP | 创建时间 | 自动记录 |
| updated_at | TIMESTAMP | 更新时间 | 自动更新 |
| deleted_at | TIMESTAMP | 软删除标记 | 非空表示已删除 |

**业务规则**:
- Agent 可以处于"编辑中"状态，此时配置未发布
- 发布后会生成一个版本记录（agent_versions表）
- published_version 指向当前生效的版本
- 软删除设计，deleted_at 非空表示已删除

#### 2. agent_versions 表（Agent版本表）

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| id | VARCHAR(36) | 版本ID | `v_uuid_xxx` |
| agent_id | VARCHAR(36) | 所属Agent | 关联 agents 表 |
| version_number | VARCHAR(20) | 语义化版本号 | `1.0.0`, `1.1.0` |
| publish_status | SMALLINT | 发布状态 | 1-审核中, 2-已发布, 3-拒绝, 4-已下架 |
| reject_reason | TEXT | 拒绝原因 | 审核未通过时填写 |
| change_log | TEXT | 更新日志 | 本版本的变更说明 |
| published_at | TIMESTAMP | 发布时间 | 审核通过的时间 |

**业务规则**:
- 每次发布都会生成一个新版本记录
- 版本记录是不可变的（Immutable），记录发布时的配置快照
- 支持版本回滚：将 agents.published_version 指向历史版本
- publish_status 管理发布流程

#### 3. agent_workspace 表（用户工作区）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | VARCHAR(36) | 主键 |
| agent_id | VARCHAR(36) | Agent ID |
| user_id | VARCHAR(36) | 用户ID |
| created_at | TIMESTAMP | 添加时间 |

**业务规则**:
- 用户可以将任何公开的 Agent 添加到自己的工作区
- UNIQUE(agent_id, user_id) 约束防止重复添加
- 类似"收藏夹"的概念

### 🆕 新增核心代码

#### 1. 领域模型 - AgentEntity（Agent聚合根）

```java
package org.xhy.domain.agent.model;

import org.xhy.domain.agent.constant.AgentStatus;
import org.xhy.domain.agent.constant.AgentType;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Agent 聚合根
 * 
 * 职责：
 * - 管理 Agent 的完整生命周期
 * - 维护 Agent 配置的一致性
 * - 提供版本管理能力
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public class AgentEntity {
    
    /** Agent ID */
    private String id;
    
    /** Agent 名称 */
    private String name;
    
    /** 头像URL */
    private String avatar;
    
    /** 描述 */
    private String description;
    
    // ========== 配置信息 ==========
    
    /** 系统提示词 */
    private String systemPrompt;
    
    /** 欢迎消息 */
    private String welcomeMessage;
    
    /** 模型配置 */
    private LLMModelConfig modelConfig;
    
    /** 工具列表 */
    private List<AgentTool> tools;
    
    /** 知识库ID列表 */
    private List<String> knowledgeBaseIds;
    
    // ========== 状态管理 ==========
    
    /** 当前发布的版本ID */
    private String publishedVersion;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** Agent 类型 */
    private AgentType agentType;
    
    // ========== 审计信息 ==========
    
    /** 创建者用户ID */
    private String userId;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 软删除标记 */
    private LocalDateTime deletedAt;
    
    /**
     * 私有构造函数
     */
    private AgentEntity() {
        this.tools = new ArrayList<>();
        this.knowledgeBaseIds = new ArrayList<>();
        this.enabled = true;
        this.agentType = AgentType.CHAT_ASSISTANT;
    }
    
    /**
     * 创建新 Agent（工厂方法）
     * 
     * @param name Agent名称
     * @param userId 创建者ID
     * @return Agent实例
     */
    public static AgentEntity create(String name, String userId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent名称不能为空");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Agent名称长度不能超过50个字符");
        }
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        
        AgentEntity agent = new AgentEntity();
        agent.setId(UUID.randomUUID().toString());
        agent.setName(name);
        agent.setUserId(userId);
        agent.setCreatedAt(LocalDateTime.now());
        agent.setUpdatedAt(LocalDateTime.now());
        
        return agent;
    }
    
    /**
     * 更新基本信息（领域行为）
     * 
     * @param name 新名称
     * @param avatar 新头像
     * @param description 新描述
     */
    public void updateBasicInfo(String name, String avatar, String description) {
        if (name != null && !name.trim().isEmpty()) {
            if (name.length() > 50) {
                throw new IllegalArgumentException("Agent名称长度不能超过50个字符");
            }
            this.name = name;
        }
        this.avatar = avatar;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新配置信息（领域行为）
     * 
     * @param systemPrompt 系统提示词
     * @param welcomeMessage 欢迎消息
     * @param modelConfig 模型配置
     * @param tools 工具列表
     * @param knowledgeBaseIds 知识库ID列表
     */
    public void updateConfig(
            String systemPrompt,
            String welcomeMessage,
            LLMModelConfig modelConfig,
            List<AgentTool> tools,
            List<String> knowledgeBaseIds) {
        this.systemPrompt = systemPrompt;
        this.welcomeMessage = welcomeMessage;
        this.modelConfig = modelConfig;
        this.tools = tools != null ? new ArrayList<>(tools) : new ArrayList<>();
        this.knowledgeBaseIds = knowledgeBaseIds != null ? 
            new ArrayList<>(knowledgeBaseIds) : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 启用 Agent（领域行为）
     */
    public void enable() {
        this.enabled = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 禁用 Agent（领域行为）
     */
    public void disable() {
        this.enabled = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 发布版本（领域行为）
     * 
     * @param versionId 版本ID
     */
    public void publishVersion(String versionId) {
        if (versionId == null || versionId.trim().isEmpty()) {
            throw new IllegalArgumentException("版本ID不能为空");
        }
        this.publishedVersion = versionId;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 软删除（领域行为）
     */
    public void delete() {
        this.deletedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 检查是否已删除
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
    
    /**
     * 检查是否可以使用
     */
    public boolean isUsable() {
        return this.enabled && !isDeleted() && this.publishedVersion != null;
    }
    
    /**
     * 添加工具
     */
    public void addTool(AgentTool tool) {
        if (tool == null) {
            throw new IllegalArgumentException("工具不能为空");
        }
        if (this.tools == null) {
            this.tools = new ArrayList<>();
        }
        this.tools.add(tool);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 移除工具
     */
    public void removeTool(String toolId) {
        if (this.tools != null) {
            this.tools.removeIf(tool -> tool.getId().equals(toolId));
            this.updatedAt = LocalDateTime.now();
        }
    }
    
    // ========== Getters and Setters ==========
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAvatar() {
        return avatar;
    }
    
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public String getWelcomeMessage() {
        return welcomeMessage;
    }
    
    public void setWelcomeMessage(String welcomeMessage) {
        this.welcomeMessage = welcomeMessage;
    }
    
    public LLMModelConfig getModelConfig() {
        return modelConfig;
    }
    
    public void setModelConfig(LLMModelConfig modelConfig) {
        this.modelConfig = modelConfig;
    }
    
    public List<AgentTool> getTools() {
        return tools;
    }
    
    public void setTools(List<AgentTool> tools) {
        this.tools = tools;
    }
    
    public List<String> getKnowledgeBaseIds() {
        return knowledgeBaseIds;
    }
    
    public void setKnowledgeBaseIds(List<String> knowledgeBaseIds) {
        this.knowledgeBaseIds = knowledgeBaseIds;
    }
    
    public String getPublishedVersion() {
        return publishedVersion;
    }
    
    public void setPublishedVersion(String publishedVersion) {
        this.publishedVersion = publishedVersion;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public AgentType getAgentType() {
        return agentType;
    }
    
    public void setAgentType(AgentType agentType) {
        this.agentType = agentType;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
```

#### 2. 枚举类

**AgentType.java** - Agent 类型枚举

```java
package org.xhy.domain.agent.constant;

/**
 * Agent 类型枚举
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public enum AgentType {
    
    /** 聊天助手 - 通用对话型 Agent */
    CHAT_ASSISTANT(1, "聊天助手", "通用对话型AI助手"),
    
    /** 功能性 Agent - 具有特定功能的 Agent */
    FUNCTIONAL_AGENT(2, "功能性Agent", "执行特定任务的AI助手");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    AgentType(Integer code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据 code 获取枚举
     */
    public static AgentType fromCode(Integer code) {
        for (AgentType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的 Agent 类型: " + code);
    }
}
```

**PublishStatus.java** - 发布状态枚举

```java
package org.xhy.domain.agent.constant;

/**
 * Agent 版本发布状态枚举
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public enum PublishStatus {
    
    /** 审核中 */
    REVIEWING(1, "审核中", "版本已提交，等待审核"),
    
    /** 已发布 */
    PUBLISHED(2, "已发布", "版本审核通过并已发布"),
    
    /** 审核拒绝 */
    REJECTED(3, "审核拒绝", "版本审核未通过"),
    
    /** 已下架 */
    OFFLINE(4, "已下架", "版本已从市场下架");
    
    private final Integer code;
    private final String name;
    private final String description;
    
    PublishStatus(Integer code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据 code 获取枚举
     */
    public static PublishStatus fromCode(Integer code) {
        for (PublishStatus status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的发布状态: " + code);
    }
    
    /**
     * 检查是否可以使用
     */
    public boolean isUsable() {
        return this == PUBLISHED;
    }
}
```

#### 3. 值对象

**AgentTool.java** - Agent 工具配置

```java
package org.xhy.domain.agent.model;

/**
 * Agent 工具配置（值对象）
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public class AgentTool {
    
    /** 工具ID */
    private String id;
    
    /** 工具名称 */
    private String name;
    
    /** 工具描述 */
    private String description;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 工具配置参数（JSON） */
    private String config;
    
    public AgentTool() {
        this.enabled = true;
    }
    
    public AgentTool(String id, String name) {
        this.id = id;
        this.name = name;
        this.enabled = true;
    }
    
    // Getters and Setters...
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getConfig() {
        return config;
    }
    
    public void setConfig(String config) {
        this.config = config;
    }
}
```

**LLMModelConfig.java** - 模型配置

```java
package org.xhy.domain.agent.model;

/**
 * LLM 模型配置（值对象）
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public class LLMModelConfig {
    
    /** 模型ID */
    private String model;
    
    /** 温度参数（0.0-2.0） */
    private Double temperature;
    
    /** 最大 Token 数 */
    private Integer maxTokens;
    
    /** Top P 参数 */
    private Double topP;
    
    /** 频率惩罚 */
    private Double frequencyPenalty;
    
    /** 存在惩罚 */
    private Double presencePenalty;
    
    /**
     * 创建默认配置
     */
    public static LLMModelConfig defaultConfig() {
        LLMModelConfig config = new LLMModelConfig();
        config.setModel("Qwen/Qwen2.5-7B-Instruct");
        config.setTemperature(0.7);
        config.setMaxTokens(2048);
        config.setTopP(1.0);
        config.setFrequencyPenalty(0.0);
        config.setPresencePenalty(0.0);
        return config;
    }
    
    /**
     * 验证配置
     */
    public void validate() {
        if (temperature != null && (temperature < 0 || temperature > 2)) {
            throw new IllegalArgumentException("温度参数必须在 0-2 之间");
        }
        if (maxTokens != null && maxTokens <= 0) {
            throw new IllegalArgumentException("最大 Token 数必须大于 0");
        }
        if (topP != null && (topP < 0 || topP > 1)) {
            throw new IllegalArgumentException("Top P 参数必须在 0-1 之间");
        }
    }
    
    // Getters and Setters...
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public Double getTemperature() {
        return temperature;
    }
    
    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
    
    public Integer getMaxTokens() {
        return maxTokens;
    }
    
    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }
    
    public Double getTopP() {
        return topP;
    }
    
    public void setTopP(Double topP) {
        this.topP = topP;
    }
    
    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }
    
    public void setFrequencyPenalty(Double frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
    }
    
    public Double getPresencePenalty() {
        return presencePenalty;
    }
    
    public void setPresencePenalty(Double presencePenalty) {
        this.presencePenalty = presencePenalty;
    }
}
```

### 📝 新增 API 接口

#### 1. 管理员接口（Admin API）

**AdminAgentController.java** - Agent 审核管理

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | `/api/admin/agents` | 获取所有 Agent 列表 | 管理员 |
| GET | `/api/admin/agents/pending` | 获取待审核 Agent 列表 | 管理员 |
| POST | `/api/admin/agents/{id}/review` | 审核 Agent 版本 | 管理员 |
| POST | `/api/admin/agents/{id}/offline` | 下架 Agent | 管理员 |
| GET | `/api/admin/agents/stats` | 获取 Agent 统计数据 | 管理员 |

**审核接口示例**:

```java
@RestController
@RequestMapping("/api/admin/agents")
public class AdminAgentController {
    
    @Autowired
    private AgentAppService agentAppService;
    
    /**
     * 审核 Agent 版本
     */
    @PostMapping("/{agentId}/versions/{versionId}/review")
    public Result<Void> reviewVersion(
            @PathVariable String agentId,
            @PathVariable String versionId,
            @RequestBody ReviewAgentVersionRequest request) {
        
        agentAppService.reviewVersion(versionId, request);
        return Result.success(null);
    }
}
```

**请求示例**:
```json
POST /api/admin/agents/{agentId}/versions/{versionId}/review
{
  "approved": true,
  "rejectReason": null
}
```

#### 2. 门户接口（Portal API）

**PortalAgentController.java** - 用户 Agent 管理

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/portal/agents` | 创建 Agent |
| GET | `/api/portal/agents` | 获取我的 Agent 列表 |
| GET | `/api/portal/agents/market` | 获取 Agent 市场列表 |
| GET | `/api/portal/agents/{id}` | 获取 Agent 详情 |
| PUT | `/api/portal/agents/{id}/basic` | 更新 Agent 基本信息 |
| PUT | `/api/portal/agents/{id}/config` | 更新 Agent 配置 |
| POST | `/api/portal/agents/{id}/publish` | 发布 Agent 版本 |
| DELETE | `/api/portal/agents/{id}` | 删除 Agent |
| POST | `/api/portal/agents/{id}/workspace` | 添加到工作区 |
| GET | `/api/portal/agents/workspace` | 获取我的工作区 Agent |

**创建 Agent 接口示例**:

```json
POST /api/portal/agents
{
  "name": "编程助手",
  "avatar": "https://example.com/avatar.png",
  "description": "一个专业的编程辅助 AI",
  "agentType": 1
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "agent_uuid_xxx",
    "name": "编程助手",
    "avatar": "https://example.com/avatar.png",
    "description": "一个专业的编程辅助 AI",
    "agentType": 1,
    "enabled": true,
    "createdAt": "2025-03-21T10:00:00"
  }
}
```

**更新配置接口示例**:

```json
PUT /api/portal/agents/{id}/config
{
  "systemPrompt": "你是一个专业的编程助手，擅长多种编程语言...",
  "welcomeMessage": "你好！我是编程助手，有什么可以帮你的吗？",
  "modelConfig": {
    "model": "Qwen/Qwen2.5-7B-Instruct",
    "temperature": 0.7,
    "maxTokens": 2048
  },
  "tools": [
    {
      "id": "code_interpreter",
      "name": "代码解释器",
      "enabled": true
    }
  ],
  "knowledgeBaseIds": ["kb_programming_docs"]
}
```

**发布版本接口示例**:

```json
POST /api/portal/agents/{id}/publish
{
  "versionNumber": "1.0.0",
  "changeLog": "初始版本发布\n- 支持多种编程语言\n- 集成代码解释器"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "versionId": "version_uuid_xxx",
    "versionNumber": "1.0.0",
    "publishStatus": 1,  // 审核中
    "createdAt": "2025-03-21T10:30:00"
  }
}
```

### ✅ 功能特性

1. ✅ **Agent CRUD 操作**
   - 创建、编辑、删除、查询 Agent
   - 软删除机制
   - 启用/禁用控制

2. ✅ **Agent 配置管理**
   - 系统提示词（System Prompt）
   - 欢迎消息
   - 模型配置（温度、最大 Token 等）
   - 工具集成
   - 知识库关联

3. ✅ **版本管理**
   - 版本发布
   - 版本历史记录
   - 版本回滚支持
   - 语义化版本号（1.0.0）

4. ✅ **发布审核流程**
   - 提交审核
   - 管理员审核
   - 审核通过/拒绝
   - 版本下架

5. ✅ **Agent 工作区**
   - 用户收藏 Agent
   - 工作区管理
   - 快速访问

6. ✅ **Agent 与会话关联**
   - 会话指定使用的 Agent
   - Agent 配置影响对话行为

7. ✅ **前端管理界面**
   - Agent 列表页
   - Agent 创建/编辑页
   - Agent 配置面板
   - 版本管理页
   - 市场浏览页

### 🎓 技术亮点

#### 1. 版本管理设计

**不可变版本**（Immutable Version）
- 每次发布都创建一个新的版本记录
- 版本记录是配置的快照，发布后不可修改
- 支持版本回滚：修改 `agents.published_version` 指向历史版本

**好处**:
- 可追溯：完整保留所有历史配置
- 可回滚：快速恢复到任意历史版本
- 可对比：对比不同版本的配置差异

#### 2. 编辑中与已发布分离

**设计理念**:
- `agents` 表存储当前"编辑中"的配置
- `agent_versions` 表存储"已发布"的版本快照
- 用户可以边使用已发布版本，边编辑新配置

**好处**:
- 不影响线上运行：编辑不影响已发布版本
- 灵活发布：编辑完成后再决定是否发布
- 草稿保存：支持保存草稿状态

#### 3. 发布审核机制

**审核流程**:
```
编辑配置 → 发布（创建版本） → 提交审核 → 管理员审核 → 审核通过 → 上架
                                              ↓
                                          审核拒绝 → 修改后重新提交
```

**状态流转**:
```java
REVIEWING(审核中) → PUBLISHED(已发布) / REJECTED(拒绝)
PUBLISHED(已发布) → OFFLINE(已下架)
```

#### 4. DDD 聚合设计

**AgentEntity 作为聚合根**:
- 管理 Agent 的完整生命周期
- 提供领域行为方法（updateConfig、publish、enable/disable）
- 保证 Agent 数据的一致性

**值对象**:
- `LLMModelConfig`: 模型配置
- `AgentTool`: 工具配置
- 使用值对象封装复杂配置，提高可维护性

#### 5. 工作区（Workspace）设计

**类似"收藏夹"的概念**:
- 用户可以将喜欢的 Agent 添加到工作区
- UNIQUE 约束防止重复添加
- 快速访问常用 Agent

**扩展性**:
- 未来可以添加分组功能
- 支持工作区排序
- 支持工作区分享

### 📊 代码统计

- **Java 类**: 75 个（+35）
  - 领域模型: 8 个
  - 仓储接口: 3 个
  - 仓储实现: 3 个
  - 应用服务: 2 个
  - 控制器: 2 个
  - 枚举: 3 个
  - DTO: 10+ 个

- **前端文件**: 128 个（+31）
  - Agent 管理页面: 5 个
  - Agent 组件: 5 个
  - TypeScript 类型定义: 更新

- **数据库表**: 6 个（+3）
  - agents
  - agent_versions
  - agent_workspace

- **API 接口**: 15+ 个

- **代码行数**: ~5000 行（+2500行）

### 🔄 与 v2.0 的差异

| 维度 | v2.0 | v2.1 |
|------|------|------|
| **核心功能** | 会话管理 | 会话管理 + Agent 管理 |
| **领域模型** | Session, Message, Context | +Agent, AgentVersion |
| **数据库表** | 3 个 | 6 个（+3） |
| **Java 文件** | 40 | 75 (+35) |
| **前端文件** | 97 | 128 (+31) |
| **API 接口** | 8 个 | 23+ 个 |
| **新增概念** | - | 版本管理、发布审核、工作区 |
| **代码复杂度** | 中等 | 较高 |

**重要变更**:
1. 引入 Agent 概念，会话可以关联 Agent
2. 实现完整的版本管理和发布流程
3. 支持 Agent 配置（提示词、工具、模型等）
4. 前端新增 Agent 管理模块

---

## v2.2 - AgentX-2025-03-21-feat-infrastructure

### 📅 版本信息
- **发布日期**: 2025-03-21
- **版本主题**: 基础设施完善
- **Java 文件数**: 81 (+6)
- **前端文件**: 129 (+1)
- **数据库表**: 6 个（无变化）
- **开发周期**: 半天
- **重大变更**: 完善基础设施，增强系统稳定性和可维护性

### 🎯 版本目标
1. 实现用户认证拦截器
2. 完善全局异常处理
3. 实现用户上下文管理（UserContext）
4. 优化 WebMvc 配置
5. 完善数据库 Schema 初始化脚本
6. 统一异常体系
7. 参数校验增强

### 📦 新增/修改文件

```
AgentX-2025-03-21-feat-infrastructure/
├── AgentX/
│   └── src/main/java/org/xhy/
│       ├── infrastructure/
│       │   ├── auth/                           # 🆕 认证相关
│       │   │   ├── UserAuthInterceptor.java   # 🆕 用户认证拦截器
│       │   │   └── UserContext.java           # 🆕 用户上下文（ThreadLocal）
│       │   ├── config/
│       │   │   ├── GlobalExceptionHandler.java # 🆕 全局异常处理
│       │   │   ├── WebMvcConfig.java          # 🆕 WebMvc 配置
│       │   │   └── WebConfig.java             # 🔄 更新
│       │   └── exception/                      # 🆕 自定义异常体系
│       │       ├── BusinessException.java      # 业务异常
│       │       ├── EntityNotFoundException.java # 实体未找到异常
│       │       └── ParamValidationException.java # 参数校验异常
│       └── domain/
│           └── common/                         # 🆕 通用领域
│               ├── exception/
│               │   ├── BusinessException.java
│               │   ├── EntityNotFoundException.java
│               │   └── ParamValidationException.java
│               └── util/
│                   └── ValidationUtils.java    # 🆕 校验工具类
└── docs/
    ├── sql/
    │   ├── init.sql                            # 🔄 更新完整脚本
    │   ├── schema_main.sql                     # 🆕 主表结构
    │   ├── schema_agent.sql                    # 🆕 Agent 表结构
    │   └── migration/                          # 🆕 数据库迁移脚本
    │       └── V1__init_tables.sql
    └── development/                            # 🆕 开发规范文档
        ├── ddd-guide.md
        └── code-style.md
```

### 🆕 新增核心代码

#### 1. 用户认证拦截器

**UserAuthInterceptor.java**

```java
package org.xhy.infrastructure.auth;

import org.springframework.web.servlet.HandlerInterceptor;
import org.xhy.infrastructure.exception.BusinessException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户认证拦截器
 * 
 * 职责：
 * - 从请求头中提取用户信息
 * - 验证用户身份
 * - 将用户信息存入 ThreadLocal
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public class UserAuthInterceptor implements HandlerInterceptor {
    
    private static final String USER_ID_HEADER = "X-User-Id";
    private static final String USER_NAME_HEADER = "X-User-Name";
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) throws Exception {
        
        // 从请求头中获取用户信息
        String userId = request.getHeader(USER_ID_HEADER);
        String userName = request.getHeader(USER_NAME_HEADER);
        
        // 健康检查等公开接口跳过认证
        String requestPath = request.getRequestURI();
        if (isPublicPath(requestPath)) {
            return true;
        }
        
        // 验证用户信息
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("用户未登录");
        }
        
        // 设置用户上下文
        UserContext.setUserId(userId);
        if (userName != null && !userName.trim().isEmpty()) {
            UserContext.setUserName(userName);
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, 
                               HttpServletResponse response, 
                               Object handler, 
                               Exception ex) throws Exception {
        // 请求结束后清理 ThreadLocal，防止内存泄漏
        UserContext.clear();
    }
    
    /**
     * 判断是否为公开路径（不需要认证）
     */
    private boolean isPublicPath(String path) {
        return path.startsWith("/health") ||
               path.startsWith("/api/public") ||
               path.startsWith("/swagger") ||
               path.startsWith("/v3/api-docs");
    }
}
```

#### 2. 用户上下文

**UserContext.java**

```java
package org.xhy.infrastructure.auth;

/**
 * 用户上下文
 * 
 * 使用 ThreadLocal 存储当前请求的用户信息
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public class UserContext {
    
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_NAME = new ThreadLocal<>();
    
    /**
     * 设置用户ID
     */
    public static void setUserId(String userId) {
        USER_ID.set(userId);
    }
    
    /**
     * 获取当前用户ID
     */
    public static String getCurrentUserId() {
        String userId = USER_ID.get();
        if (userId == null) {
            throw new BusinessException("用户上下文未初始化");
        }
        return userId;
    }
    
    /**
     * 设置用户名
     */
    public static void setUserName(String userName) {
        USER_NAME.set(userName);
    }
    
    /**
     * 获取当前用户名
     */
    public static String getCurrentUserName() {
        return USER_NAME.get();
    }
    
    /**
     * 清理上下文
     * 
     * 必须在请求结束后调用，防止 ThreadLocal 内存泄漏
     */
    public static void clear() {
        USER_ID.remove();
        USER_NAME.remove();
    }
}
```

#### 3. 全局异常处理

**GlobalExceptionHandler.java**

```java
package org.xhy.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.xhy.infrastructure.exception.BusinessException;
import org.xhy.infrastructure.exception.EntityNotFoundException;
import org.xhy.infrastructure.exception.ParamValidationException;
import org.xhy.interfaces.api.common.Result;

/**
 * 全局异常处理器
 * 
 * 统一处理各种异常，返回标准的错误响应
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e) {
        logger.warn("业务异常: {}", e.getMessage());
        return Result.error(e.getMessage());
    }
    
    /**
     * 处理实体未找到异常
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleEntityNotFoundException(EntityNotFoundException e) {
        logger.warn("实体未找到: {}", e.getMessage());
        return Result.error(404, e.getMessage());
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(ParamValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleParamValidationException(ParamValidationException e) {
        logger.warn("参数校验失败: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }
    
    /**
     * 处理 Spring Validation 异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        logger.warn("参数校验失败: {}", message);
        return Result.error(400, message);
    }
    
    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("非法参数: {}", e.getMessage());
        return Result.error(400, e.getMessage());
    }
    
    /**
     * 处理未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        logger.error("系统异常", e);
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}
```

#### 4. 自定义异常类

**BusinessException.java** - 业务异常

```java
package org.xhy.infrastructure.exception;

/**
 * 业务异常
 * 
 * 用于业务逻辑中的异常情况
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public class BusinessException extends RuntimeException {
    
    private Integer code;
    
    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 400;
    }
    
    public Integer getCode() {
        return code;
    }
}
```

**EntityNotFoundException.java** - 实体未找到异常

```java
package org.xhy.infrastructure.exception;

/**
 * 实体未找到异常
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
public class EntityNotFoundException extends BusinessException {
    
    public EntityNotFoundException(String entityName, String id) {
        super(String.format("%s[id=%s]不存在", entityName, id));
    }
    
    public EntityNotFoundException(String message) {
        super(message);
    }
}
```

#### 5. WebMvc 配置

**WebMvcConfig.java**

```java
package org.xhy.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.xhy.infrastructure.auth.UserAuthInterceptor;

/**
 * WebMvc 配置
 * 
 * @author AgentX Team
 * @since 2025-03-21
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserAuthInterceptor())
                .addPathPatterns("/api/**")  // 拦截所有 API 请求
                .excludePathPatterns(
                    "/health/**",           // 排除健康检查
                    "/api/public/**"        // 排除公开接口
                );
    }
    
    /**
     * 配置 CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")  // 允许前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
```

### ✅ 功能特性

1. ✅ **用户认证**
   - 请求头认证（X-User-Id, X-User-Name）
   - 用户上下文管理（ThreadLocal）
   - 公开路径白名单

2. ✅ **全局异常处理**
   - 统一异常响应格式
   - 异常分类处理
   - 详细日志记录

3. ✅ **自定义异常体系**
   - BusinessException（业务异常）
   - EntityNotFoundException（实体未找到）
   - ParamValidationException（参数校验）

4. ✅ **WebMvc 增强**
   - 拦截器配置
   - CORS 跨域配置
   - 路径匹配规则

5. ✅ **数据库脚本管理**
   - 分模块 SQL 脚本
   - 数据库迁移支持
   - 完整注释和文档

6. ✅ **开发规范文档**
   - DDD 开发指南
   - 代码风格规范
   - 数据库设计规范

### 🎓 技术亮点

#### 1. ThreadLocal 用户上下文

**优势**:
- 线程隔离：每个请求独立的用户信息
- 无侵入：业务代码无需传递 userId 参数
- 自动清理：请求结束后自动释放

**注意事项**:
- 必须在请求结束后调用 `clear()`
- 防止线程池复用导致的数据污染

#### 2. 统一异常处理

**分层异常处理**:
```
领域层 → 抛出领域异常（IllegalArgumentException、业务异常）
应用层 → 编排业务，传递异常
接口层 → GlobalExceptionHandler 捕获并转换为 HTTP 响应
```

**好处**:
- 统一错误响应格式
- 自动记录异常日志
- 友好的错误提示

#### 3. 拦截器设计

**职责清晰**:
- `preHandle`: 认证和授权
- `afterCompletion`: 资源清理

**灵活配置**:
- 路径模式匹配
- 白名单机制
- 可扩展多个拦截器

### 📊 代码统计

- **Java 类**: 81 个（+6）
- **新增基础设施类**: 6 个
- **SQL 脚本**: 4 个文件
- **文档**: 2 个开发规范文档

### 🔄 与 v2.1 的差异

| 维度 | v2.1 | v2.2 |
|------|------|------|
| **核心功能** | Agent 管理 | Agent 管理 + 基础设施 |
| **认证机制** | 无 | 用户认证拦截器 |
| **异常处理** | 基础 | 全局统一处理 |
| **用户上下文** | 无 | ThreadLocal 实现 |
| **CORS 配置** | 基础 | 完善配置 |
| **代码质量** | 良好 | 优秀 |

**重要变更**:
1. 引入用户认证机制
2. 完善异常处理体系
3. 优化开发体验

---

## v2.3 - AgentX-2025-03-22-feat-agent-message

### 📅 版本信息
- **发布日期**: 2025-03-22
- **版本主题**: Agent 消息优化
- **Java 文件数**: 79 (-2，代码优化精简）
- **前端文件**: 136 (+7)
- **数据库表**: 6 个（无变化）
- **开发周期**: 半天
- **重大变更**: 优化 Agent 与消息的交互，改进消息处理流程

### 🎯 版本目标
1. 优化 Agent 消息处理流程
2. 改进消息存储机制
3. Agent 系统提示词注入
4. 优化前端消息展示
5. 性能优化

### 🆕 主要变更

#### 1. Agent 消息处理优化

**核心改进**:
- 会话创建时自动注入 Agent 的系统提示词
- 消息发送时应用 Agent 的模型配置
- 工具调用集成准备

**ConversationAppService.java** - 更新

```java
@Service
public class ConversationAppService {
    
    @Autowired
    private AgentService agentService;
    
    @Autowired
    private SessionService sessionService;
    
    @Autowired
    private MessageService messageService;
    
    @Autowired
    private LlmService llmService;
    
    /**
     * 使用 Agent 创建会话并发送第一条消息
     */
    @Transactional
    public ChatResponse createAndChat(CreateAndChatRequest request) {
        // 1. 获取 Agent
        AgentEntity agent = agentService.getById(request.getAgentId());
        if (!agent.isUsable()) {
            throw new BusinessException("Agent 不可用");
        }
        
        // 2. 创建会话
        Session session = sessionService.createSessionWithAgent(
            request.getTitle(),
            UserContext.getCurrentUserId(),
            agent.getId()
        );
        
        // 3. 注入系统提示词（如果 Agent 有配置）
        if (agent.getSystemPrompt() != null && !agent.getSystemPrompt().isEmpty()) {
            Message systemMessage = Message.createSystemMessage(
                session.getId(),
                agent.getSystemPrompt()
            );
            messageService.save(systemMessage);
        }
        
        // 4. 保存用户消息
        Message userMessage = messageService.saveUserMessage(
            session.getId(),
            request.getMessage()
        );
        
        // 5. 获取会话历史
        List<Message> history = messageService.getSessionMessages(session.getId());
        
        // 6. 构建 LLM 请求（使用 Agent 的模型配置）
        LlmRequest llmRequest = buildLlmRequest(agent, history);
        
        // 7. 调用 LLM
        LlmResponse llmResponse = llmService.chat(llmRequest);
        
        // 8. 保存 AI 回复
        Message assistantMessage = messageService.saveAssistantMessage(
            session.getId(),
            llmResponse.getContent(),
            llmResponse.getModel(),
            llmResponse.getTotalTokens()
        );
        
        // 9. 返回响应
        ChatResponse response = new ChatResponse();
        response.setSessionId(session.getId());
        response.setMessage(llmResponse.getContent());
        response.setTokens(llmResponse.getTotalTokens());
        response.setMessageId(assistantMessage.getId());
        
        return response;
    }
    
    /**
     * 根据 Agent 配置构建 LLM 请求
     */
    private LlmRequest buildLlmRequest(AgentEntity agent, List<Message> history) {
        LLMModelConfig modelConfig = agent.getModelConfig() != null ? 
            agent.getModelConfig() : LLMModelConfig.defaultConfig();
        
        List<LlmMessage> llmMessages = history.stream()
            .map(msg -> new LlmMessage(msg.getRole(), msg.getContent()))
            .collect(Collectors.toList());
        
        return LlmRequest.builder()
            .model(modelConfig.getModel())
            .messages(llmMessages)
            .temperature(modelConfig.getTemperature())
            .maxTokens(modelConfig.getMaxTokens())
            .build();
    }
}
```

#### 2. 前端消息优化

**MessageItem.tsx** - 优化消息展示

```typescript
interface MessageItemProps {
  message: Message;
  agent?: Agent;
}

export function MessageItem({ message, agent }: MessageItemProps) {
  return (
    <div className={cn(
      "flex gap-3 p-4",
      message.role === 'user' ? 'justify-end' : 'justify-start'
    )}>
      {/* Assistant 消息显示 Agent 头像 */}
      {message.role === 'assistant' && agent && (
        <Avatar>
          <AvatarImage src={agent.avatar} />
          <AvatarFallback>{agent.name[0]}</AvatarFallback>
        </Avatar>
      )}
      
      <div className={cn(
        "max-w-[70%] rounded-lg p-3",
        message.role === 'user' 
          ? "bg-blue-500 text-white" 
          : "bg-gray-100 text-gray-900"
      )}>
        {/* Markdown 渲染 */}
        <ReactMarkdown>{message.content}</ReactMarkdown>
        
        {/* Token 统计 */}
        {message.tokenCount && (
          <div className="text-xs opacity-70 mt-2">
            {message.tokenCount} tokens
          </div>
        )}
      </div>
      
      {/* User 消息显示用户头像 */}
      {message.role === 'user' && (
        <Avatar>
          <AvatarFallback>U</AvatarFallback>
        </Avatar>
      )}
    </div>
  );
}
```

### ✅ 功能特性

1. ✅ **Agent 系统提示词自动注入**
2. ✅ **Agent 模型配置应用**
3. ✅ **改进的消息展示（Markdown 支持）**
4. ✅ **Agent 头像显示**
5. ✅ **Token 使用统计展示**
6. ✅ **代码优化和精简**

### 📊 代码统计

- **Java 类**: 79 个（-2，优化精简）
- **前端组件**: 136 个（+7）
- **优化**: 重构了消息处理流程

### 🔄 与 v2.2 的差异

| 维度 | v2.2 | v2.3 |
|------|------|------|
| **消息处理** | 基础 | Agent 配置集成 |
| **系统提示词** | 手动 | 自动注入 |
| **消息展示** | 纯文本 | Markdown 支持 |
| **Agent 集成** | 基础 | 深度集成 |

---

**第二阶段文档完成！** ✅

后续将继续生成：
- 第三阶段：LLM 服务抽象与优化
- 第四阶段：Agent 高级功能
- ...

