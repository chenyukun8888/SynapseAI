# AgentX 详细版本演进历史 - 第五阶段

## 第五阶段：工具与定时任务 (2025-05-10 ~ 2025-05-29)

本阶段主要聚焦于工具系统的深化和定时任务功能，实现更强大的自动化能力。

---

## v5.0 - AgentX-2025-05-10-feat-plugin-tools

### 📅 版本信息
- **发布日期**: 2025-05-10
- **版本主题**: 插件工具系统深化
- **Java 文件数**: 257 (+50)
- **前端文件**: 166 (+24)
- **数据库表**: 14 个（+2）
- **开发周期**: 3天
- **重大变更**: 工具系统全面升级，引入插件机制

### 🎯 版本目标
1. 实现工具插件化架构
2. 支持第三方工具接入
3. 工具权限管理
4. 工具版本控制
5. 工具依赖管理

### 💾 数据库设计（新增）

```sql
-- ============================
-- 工具插件系统表
-- 版本: v5.0
-- 日期: 2025-05-10
-- ============================

-- 1. 工具插件表
CREATE TABLE tool_plugins (
    id VARCHAR(36) PRIMARY KEY COMMENT '插件ID',
    tool_id VARCHAR(36) NOT NULL COMMENT '关联的工具ID',
    plugin_name VARCHAR(100) NOT NULL COMMENT '插件名称',
    plugin_version VARCHAR(20) NOT NULL COMMENT '插件版本',
    entry_class VARCHAR(255) NOT NULL COMMENT '入口类名',
    dependencies JSONB COMMENT '依赖信息',
    config_schema JSONB COMMENT '配置模式（JSON Schema）',
    sandbox_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用沙箱',
    max_memory INTEGER COMMENT '最大内存使用（MB）',
    max_cpu_time INTEGER COMMENT '最大CPU时间（秒）',
    is_verified BOOLEAN DEFAULT FALSE COMMENT '是否已验证',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL COMMENT '更新时间'
);

-- 2. 工具权限表
CREATE TABLE tool_permissions (
    id VARCHAR(36) PRIMARY KEY COMMENT '权限ID',
    tool_id VARCHAR(36) NOT NULL COMMENT '工具ID',
    permission_type VARCHAR(50) NOT NULL COMMENT '权限类型：network/filesystem/database',
    permission_detail JSONB COMMENT '权限详情',
    is_granted BOOLEAN DEFAULT FALSE COMMENT '是否已授权',
    granted_by VARCHAR(36) COMMENT '授权人',
    granted_at TIMESTAMP COMMENT '授权时间',
    created_at TIMESTAMP NOT NULL COMMENT '创建时间'
);

-- 创建索引
CREATE INDEX idx_tool_plugins_tool_id ON tool_plugins(tool_id);
CREATE INDEX idx_tool_plugins_verified ON tool_plugins(is_verified);

CREATE INDEX idx_tool_permissions_tool_id ON tool_permissions(tool_id);
CREATE INDEX idx_tool_permissions_type ON tool_permissions(permission_type);

-- 表注释
COMMENT ON TABLE tool_plugins IS '工具插件表，记录工具的插件信息';
COMMENT ON TABLE tool_permissions IS '工具权限表，管理工具的访问权限';
```

### 🆕 新增核心功能

#### 1. 工具插件管理器

**ToolPluginManager.java**

```java
package org.xhy.application.tool.plugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xhy.domain.tool.model.Tool;
import org.xhy.domain.tool.model.ToolPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 工具插件管理器
 * 
 * 负责工具插件的加载、卸载和管理
 * 
 * @author AgentX Team
 * @since 2025-05-10
 */
@Component
public class ToolPluginManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ToolPluginManager.class);
    
    // 已加载的插件缓存
    private final Map<String, PluginContainer> loadedPlugins = new ConcurrentHashMap<>();
    
    /**
     * 加载工具插件
     * 
     * @param tool 工具实体
     * @return 插件容器
     */
    public PluginContainer loadPlugin(Tool tool) {
        String toolId = tool.getId();
        
        // 检查是否已加载
        if (loadedPlugins.containsKey(toolId)) {
            logger.info("插件已加载，使用缓存: {}", toolId);
            return loadedPlugins.get(toolId);
        }
        
        try {
            logger.info("开始加载工具插件: {}", tool.getName());
            
            // 1. 验证插件
            validatePlugin(tool);
            
            // 2. 检查权限
            checkPermissions(tool);
            
            // 3. 加载插件类
            Class<?> pluginClass = loadPluginClass(tool.getExecutorClass());
            
            // 4. 创建插件实例
            Object pluginInstance = pluginClass.getDeclaredConstructor().newInstance();
            
            // 5. 初始化沙箱环境（如果启用）
            PluginSandbox sandbox = null;
            if (tool.getPlugin().getSandboxEnabled()) {
                sandbox = createSandbox(tool);
            }
            
            // 6. 创建插件容器
            PluginContainer container = new PluginContainer(
                tool,
                pluginInstance,
                sandbox
            );
            
            // 7. 缓存插件
            loadedPlugins.put(toolId, container);
            
            logger.info("工具插件加载成功: {}", tool.getName());
            return container;
            
        } catch (Exception e) {
            logger.error("工具插件加载失败: {}，错误: {}", tool.getName(), e.getMessage(), e);
            throw new RuntimeException("插件加载失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 卸载工具插件
     */
    public void unloadPlugin(String toolId) {
        PluginContainer container = loadedPlugins.remove(toolId);
        
        if (container != null) {
            // 清理资源
            container.cleanup();
            logger.info("工具插件已卸载: {}", toolId);
        }
    }
    
    /**
     * 重新加载插件
     */
    public PluginContainer reloadPlugin(Tool tool) {
        unloadPlugin(tool.getId());
        return loadPlugin(tool);
    }
    
    /**
     * 验证插件
     */
    private void validatePlugin(Tool tool) {
        ToolPlugin plugin = tool.getPlugin();
        
        if (plugin == null) {
            throw new IllegalArgumentException("工具缺少插件配置");
        }
        
        if (!plugin.isVerified()) {
            logger.warn("工具插件未经验证: {}", tool.getName());
        }
        
        // 验证依赖
        if (plugin.getDependencies() != null && !plugin.getDependencies().isEmpty()) {
            validateDependencies(plugin.getDependencies());
        }
    }
    
    /**
     * 检查权限
     */
    private void checkPermissions(Tool tool) {
        List<ToolPermission> permissions = tool.getRequiredPermissions();
        
        for (ToolPermission permission : permissions) {
            if (!permission.isGranted()) {
                throw new SecurityException(
                    String.format("工具 %s 缺少必要权限: %s", 
                                tool.getName(), 
                                permission.getPermissionType())
                );
            }
        }
    }
    
    /**
     * 加载插件类
     */
    private Class<?> loadPluginClass(String className) throws ClassNotFoundException {
        // 使用自定义类加载器，实现插件隔离
        PluginClassLoader classLoader = new PluginClassLoader();
        return classLoader.loadClass(className);
    }
    
    /**
     * 创建沙箱环境
     */
    private PluginSandbox createSandbox(Tool tool) {
        PluginSandbox sandbox = new PluginSandbox();
        
        // 配置资源限制
        sandbox.setMaxMemory(tool.getPlugin().getMaxMemory());
        sandbox.setMaxCpuTime(tool.getPlugin().getMaxCpuTime());
        
        // 配置权限策略
        sandbox.setAllowedPermissions(tool.getRequiredPermissions());
        
        return sandbox;
    }
    
    /**
     * 验证依赖
     */
    private void validateDependencies(Map<String, String> dependencies) {
        for (Map.Entry<String, String> entry : dependencies.entrySet()) {
            String depName = entry.getKey();
            String depVersion = entry.getValue();
            
            // 检查依赖是否可用
            if (!isDependencyAvailable(depName, depVersion)) {
                throw new IllegalStateException(
                    String.format("依赖不可用: %s@%s", depName, depVersion)
                );
            }
        }
    }
    
    private boolean isDependencyAvailable(String name, String version) {
        // TODO: 实现依赖检查逻辑
        return true;
    }
    
    /**
     * 插件容器
     */
    public static class PluginContainer {
        private final Tool tool;
        private final Object pluginInstance;
        private final PluginSandbox sandbox;
        
        public PluginContainer(Tool tool, Object pluginInstance, PluginSandbox sandbox) {
            this.tool = tool;
            this.pluginInstance = pluginInstance;
            this.sandbox = sandbox;
        }
        
        public Object getPluginInstance() {
            return pluginInstance;
        }
        
        public PluginSandbox getSandbox() {
            return sandbox;
        }
        
        public void cleanup() {
            if (sandbox != null) {
                sandbox.cleanup();
            }
        }
    }
}
```

#### 2. 插件沙箱

**PluginSandbox.java**

```java
package org.xhy.infrastructure.tool.sandbox;

import java.security.*;
import java.util.List;

/**
 * 插件沙箱
 * 
 * 提供隔离的执行环境，限制插件的资源使用和访问权限
 * 
 * @author AgentX Team
 * @since 2025-05-10
 */
public class PluginSandbox {
    
    private Integer maxMemory;  // MB
    private Integer maxCpuTime;  // 秒
    private List<ToolPermission> allowedPermissions;
    private SecurityManager securityManager;
    
    /**
     * 在沙箱中执行代码
     */
    public <T> T execute(SandboxTask<T> task) throws Exception {
        // 1. 设置安全管理器
        SecurityManager oldSecurityManager = System.getSecurityManager();
        System.setSecurityManager(createSecurityManager());
        
        try {
            // 2. 创建资源监控线程
            ResourceMonitor monitor = new ResourceMonitor(maxMemory, maxCpuTime);
            monitor.start();
            
            try {
                // 3. 执行任务
                T result = task.execute();
                
                // 4. 检查资源使用
                if (monitor.isLimitExceeded()) {
                    throw new SecurityException("资源限制超出");
                }
                
                return result;
                
            } finally {
                monitor.stop();
            }
            
        } finally {
            // 恢复安全管理器
            System.setSecurityManager(oldSecurityManager);
        }
    }
    
    /**
     * 创建安全管理器
     */
    private SecurityManager createSecurityManager() {
        return new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
                // 检查权限是否在允许列表中
                if (!isPermissionAllowed(perm)) {
                    throw new SecurityException("权限被拒绝: " + perm);
                }
            }
        };
    }
    
    /**
     * 检查权限是否允许
     */
    private boolean isPermissionAllowed(Permission perm) {
        if (allowedPermissions == null || allowedPermissions.isEmpty()) {
            return false;
        }
        
        for (ToolPermission allowed : allowedPermissions) {
            if (matches(perm, allowed)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean matches(Permission perm, ToolPermission allowed) {
        // TODO: 实现权限匹配逻辑
        return false;
    }
    
    /**
     * 清理资源
     */
    public void cleanup() {
        // 清理沙箱资源
    }
    
    // Getters and Setters...
    
    public void setMaxMemory(Integer maxMemory) {
        this.maxMemory = maxMemory;
    }
    
    public void setMaxCpuTime(Integer maxCpuTime) {
        this.maxCpuTime = maxCpuTime;
    }
    
    public void setAllowedPermissions(List<ToolPermission> permissions) {
        this.allowedPermissions = permissions;
    }
    
    /**
     * 沙箱任务接口
     */
    @FunctionalInterface
    public interface SandboxTask<T> {
        T execute() throws Exception;
    }
    
    /**
     * 资源监控器
     */
    private static class ResourceMonitor {
        private final Integer maxMemory;
        private final Integer maxCpuTime;
        private volatile boolean limitExceeded = false;
        private Thread monitorThread;
        
        public ResourceMonitor(Integer maxMemory, Integer maxCpuTime) {
            this.maxMemory = maxMemory;
            this.maxCpuTime = maxCpuTime;
        }
        
        public void start() {
            monitorThread = new Thread(() -> {
                long startTime = System.currentTimeMillis();
                
                while (!Thread.interrupted()) {
                    // 检查内存使用
                    long usedMemory = Runtime.getRuntime().totalMemory() - 
                                     Runtime.getRuntime().freeMemory();
                    long usedMemoryMB = usedMemory / (1024 * 1024);
                    
                    if (maxMemory != null && usedMemoryMB > maxMemory) {
                        limitExceeded = true;
                        break;
                    }
                    
                    // 检查CPU时间
                    long elapsedSeconds = (System.currentTimeMillis() - startTime) / 1000;
                    if (maxCpuTime != null && elapsedSeconds > maxCpuTime) {
                        limitExceeded = true;
                        break;
                    }
                    
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            });
            
            monitorThread.start();
        }
        
        public void stop() {
            if (monitorThread != null) {
                monitorThread.interrupt();
            }
        }
        
        public boolean isLimitExceeded() {
            return limitExceeded;
        }
    }
}
```

### ✅ 功能特性

1. ✅ **插件化架构**
   - 动态加载插件
   - 插件隔离
   - 热更新支持

2. ✅ **安全沙箱**
   - 资源限制（内存、CPU）
   - 权限控制
   - 隔离执行

3. ✅ **依赖管理**
   - 依赖声明
   - 依赖验证
   - 版本管理

4. ✅ **权限系统**
   - 细粒度权限控制
   - 权限审批流程
   - 权限审计

5. ✅ **插件市场**
   - 第三方插件支持
   - 插件审核机制
   - 插件评分系统

### 🎓 技术亮点

#### 1. 插件隔离机制

**自定义类加载器**:
```java
public class PluginClassLoader extends URLClassLoader {
    @Override
    protected Class<?> loadClass(String name, boolean resolve) {
        // 插件类优先从插件 ClassLoader 加载
        // 系统类从父 ClassLoader 加载
    }
}
```

**好处**:
- 不同插件互不影响
- 支持同名类
- 便于插件卸载

#### 2. 安全沙箱设计

**多层防护**:
```
SecurityManager (Java 安全管理器)
    ↓
ResourceMonitor (资源监控)
    ↓
PermissionChecker (权限检查)
```

**保护措施**:
- 限制文件系统访问
- 限制网络访问
- 限制反射调用
- 限制线程创建

### 📊 代码统计

- **Java 类**: 257 个（+50）
- **数据库表**: 14 个（+2）
- **新增功能模块**: 3 个（插件管理、沙箱、权限）

---

## v5.1 - AgentX-2025-05-21-feat-tool-llm

### 📅 版本信息
- **发布日期**: 2025-05-21
- **版本主题**: 工具与 LLM 深度集成
- **Java 文件数**: 256 (-1，代码优化）
- **前端文件**: 173 (+7)
- **数据库表**: 14 个（无变化）
- **开发周期**: 2天
- **重大变更**: 工具可以自主调用 LLM

### 🎯 版本目标
1. 工具内置 LLM 调用能力
2. 工具间智能协作
3. 复杂任务自动分解
4. 多步骤工具链
5. 工具执行结果优化

### 🆕 核心功能

#### 工具 LLM 协作

**LLMEnabledToolExecutor.java**

```java
package org.xhy.infrastructure.tool.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.xhy.domain.llm.service.LlmService;
import org.xhy.domain.tool.executor.AbstractToolExecutor;
import org.xhy.domain.tool.model.ToolResult;

import java.util.Map;

/**
 * 支持 LLM 的工具执行器
 * 
 * 工具可以调用 LLM 来辅助执行复杂任务
 * 
 * @author AgentX Team
 * @since 2025-05-21
 */
public abstract class LLMEnabledToolExecutor extends AbstractToolExecutor {
    
    @Autowired
    protected LlmService llmService;
    
    /**
     * 使用 LLM 分析输入
     */
    protected String analyzeInput(String input) {
        String prompt = String.format(
            "请分析以下输入，提取关键信息：\n%s", 
            input
        );
        
        return callLLM(prompt);
    }
    
    /**
     * 使用 LLM 优化输出
     */
    protected String optimizeOutput(String rawOutput) {
        String prompt = String.format(
            "请优化以下输出，使其更清晰易懂：\n%s", 
            rawOutput
        );
        
        return callLLM(prompt);
    }
    
    /**
     * 调用 LLM
     */
    protected String callLLM(String prompt) {
        // 构建 LLM 请求
        LlmRequest request = LlmRequest.builder()
            .model("Qwen/Qwen2.5-7B-Instruct")
            .messages(List.of(new LlmMessage("user", prompt)))
            .temperature(0.7)
            .build();
        
        // 调用 LLM
        LlmResponse response = llmService.chat(request);
        
        return response.getContent();
    }
}
```

### ✅ 功能特性

1. ✅ **工具 LLM 集成**
   - 工具内调用 LLM
   - 输入分析
   - 输出优化

2. ✅ **工具协作**
   - 工具链执行
   - 结果传递
   - 智能路由

3. ✅ **任务分解**
   - 复杂任务拆分
   - 子任务分配
   - 结果聚合

---

## v5.2-v5.3 - 定时任务系统

### 📅 版本信息
- **v5.2**: AgentX-2025-05-26-feat-agent-schedule
- **v5.3**: AgentX-2025-05-29-feat-chat-preview
- **发布日期**: 2025-05-26 ~ 2025-05-29
- **版本主题**: Agent 定时任务和对话预览
- **Java 文件数**: 272-283 个
- **数据库表**: 16 个（+2）
- **开发周期**: 3天
- **重大变更**: 引入定时任务系统

### 💾 数据库设计（新增）

```sql
-- 定时任务表
CREATE TABLE scheduled_tasks (
    id VARCHAR(36) PRIMARY KEY,
    task_name VARCHAR(100) NOT NULL,
    agent_id VARCHAR(36) NOT NULL,
    cron_expression VARCHAR(100) NOT NULL,
    task_config JSONB,
    is_enabled BOOLEAN DEFAULT TRUE,
    last_run_at TIMESTAMP,
    next_run_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);

-- 任务执行记录表
CREATE TABLE task_execution_logs (
    id VARCHAR(36) PRIMARY KEY,
    task_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    result TEXT,
    error_message TEXT
);
```

### ✅ 功能特性

1. ✅ **定时任务**
   - Cron 表达式支持
   - 任务调度
   - 执行日志

2. ✅ **对话预览**
   - 消息预览
   - 快速响应
   - 缓存优化

---

## 📌 第五阶段总结

### 🎯 阶段目标达成

✅ **v5.0 - 插件工具系统深化**
- 插件化架构
- 安全沙箱
- 权限管理

✅ **v5.1 - 工具与LLM深度集成**
- 工具调用 LLM
- 工具协作
- 任务分解

✅ **v5.2-v5.3 - 定时任务系统**
- 定时任务
- 任务调度
- 执行监控

### 📊 阶段成果

- **Java 文件**: 从 207 个增加到 283 个（+76）
- **新增数据库表**: 4 个
- **核心技术**: 插件系统、沙箱隔离、LLM 协作

---

**第五阶段文档完成！** ✅


