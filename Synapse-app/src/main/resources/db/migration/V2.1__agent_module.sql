-- SynapseAI v2.1 Agent模块 - 数据库迁移脚本
-- PostgreSQL 版本

-- 1. 为sessions表添加agent_id字段
ALTER TABLE sessions ADD COLUMN IF NOT EXISTS agent_id VARCHAR(36);
CREATE INDEX IF NOT EXISTS idx_sessions_agent_id ON sessions(agent_id);
COMMENT ON COLUMN sessions.agent_id IS '关联的Agent ID，指定该会话使用的Agent';

-- 2. 创建agents表
CREATE TABLE IF NOT EXISTS agents (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    avatar VARCHAR(255),
    description TEXT,
    -- 当前编辑中的配置
    system_prompt TEXT,
    welcome_message TEXT,
    model_config JSONB,
    tools JSONB,
    knowledge_base_ids JSONB,
    -- 版本管理
    published_version VARCHAR(36), -- 当前发布的版本ID
    -- Agent状态：false-禁用，true-启用
    enabled BOOLEAN DEFAULT TRUE,
    -- Agent类型：1-聊天助手, 2-功能性Agent
    agent_type SMALLINT DEFAULT 1,
    user_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP -- 软删除标记
);

-- 3. 创建agent_versions表
CREATE TABLE IF NOT EXISTS agent_versions (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(36) NOT NULL,
    name VARCHAR(50) NOT NULL,
    avatar VARCHAR(255),
    description TEXT,
    version_number VARCHAR(20) NOT NULL, -- 版本号，如1.0.0
    system_prompt TEXT,
    welcome_message TEXT,
    model_config JSONB,
    tools JSONB,
    knowledge_base_ids JSONB,
    change_log TEXT, -- 版本更新日志
    agent_type SMALLINT DEFAULT 1, -- Agent类型：1-聊天助手, 2-功能性Agent
    publish_status SMALLINT DEFAULT 1, -- 1-审核中, 2-已发布, 3-拒绝, 4-已下架
    reject_reason TEXT,
    review_time TIMESTAMP,
    published_at TIMESTAMP, -- 发布时间
    user_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP, -- 软删除标记
    CONSTRAINT fk_agent_versions_agent FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE
);

-- 4. 创建agent_workspace表
CREATE TABLE IF NOT EXISTS agent_workspace (
    id VARCHAR(36) PRIMARY KEY,
    agent_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_agent_workspace_agent FOREIGN KEY (agent_id) REFERENCES agents(id) ON DELETE CASCADE,
    UNIQUE (agent_id, user_id)
);

-- 5. 创建索引
CREATE INDEX IF NOT EXISTS idx_agents_user_id ON agents(user_id);
CREATE INDEX IF NOT EXISTS idx_agents_enabled ON agents(enabled);
CREATE INDEX IF NOT EXISTS idx_agents_agent_type ON agents(agent_type);
CREATE INDEX IF NOT EXISTS idx_agents_name ON agents(name);
CREATE INDEX IF NOT EXISTS idx_agents_deleted_at ON agents(deleted_at);

CREATE INDEX IF NOT EXISTS idx_agent_versions_agent_id ON agent_versions(agent_id);
CREATE INDEX IF NOT EXISTS idx_agent_versions_published_at ON agent_versions(published_at);
CREATE INDEX IF NOT EXISTS idx_agent_versions_publish_status ON agent_versions(publish_status);
CREATE INDEX IF NOT EXISTS idx_agent_versions_deleted_at ON agent_versions(deleted_at);

CREATE INDEX IF NOT EXISTS idx_agent_workspace_user_id ON agent_workspace(user_id);
CREATE INDEX IF NOT EXISTS idx_agent_workspace_agent_id ON agent_workspace(agent_id);

-- 6. 添加表和列的注释
COMMENT ON TABLE agents IS 'Agent表，存储AI助手的基本信息和配置';
COMMENT ON COLUMN agents.id IS 'Agent唯一ID';
COMMENT ON COLUMN agents.name IS 'Agent名称';
COMMENT ON COLUMN agents.avatar IS '头像';
COMMENT ON COLUMN agents.description IS '描述';
COMMENT ON COLUMN agents.system_prompt IS '系统提示词';
COMMENT ON COLUMN agents.welcome_message IS '欢迎消息';
COMMENT ON COLUMN agents.model_config IS '模型配置，JSON格式，包含模型类型、温度等参数';
COMMENT ON COLUMN agents.tools IS 'Agent可使用的工具列表，JSON格式';
COMMENT ON COLUMN agents.knowledge_base_ids IS '关联的知识库ID列表，JSON格式';
COMMENT ON COLUMN agents.published_version IS '当前发布的版本ID';
COMMENT ON COLUMN agents.enabled IS 'Agent状态：false-禁用，true-启用';
COMMENT ON COLUMN agents.agent_type IS 'Agent类型：1-聊天助手, 2-功能性Agent';
COMMENT ON COLUMN agents.user_id IS '用户ID';
COMMENT ON COLUMN agents.created_at IS '创建时间';
COMMENT ON COLUMN agents.updated_at IS '更新时间';
COMMENT ON COLUMN agents.deleted_at IS '软删除标记，非空表示已删除';

COMMENT ON TABLE agent_versions IS 'Agent版本表，记录Agent的各个版本';
COMMENT ON COLUMN agent_versions.id IS '版本唯一ID';
COMMENT ON COLUMN agent_versions.agent_id IS 'Agent ID';
COMMENT ON COLUMN agent_versions.name IS 'Agent名称';
COMMENT ON COLUMN agent_versions.avatar IS '头像';
COMMENT ON COLUMN agent_versions.description IS '描述';
COMMENT ON COLUMN agent_versions.version_number IS '版本号，如1.0.0';
COMMENT ON COLUMN agent_versions.system_prompt IS '系统提示词';
COMMENT ON COLUMN agent_versions.welcome_message IS '欢迎消息';
COMMENT ON COLUMN agent_versions.model_config IS '模型配置，JSON格式';
COMMENT ON COLUMN agent_versions.tools IS '工具配置，JSON格式';
COMMENT ON COLUMN agent_versions.knowledge_base_ids IS '知识库ID列表，JSON格式';
COMMENT ON COLUMN agent_versions.change_log IS '版本更新日志';
COMMENT ON COLUMN agent_versions.agent_type IS 'Agent类型：1-聊天助手, 2-功能性Agent';
COMMENT ON COLUMN agent_versions.publish_status IS '发布状态：1-审核中, 2-已发布, 3-拒绝, 4-已下架';
COMMENT ON COLUMN agent_versions.reject_reason IS '审核拒绝原因';
COMMENT ON COLUMN agent_versions.review_time IS '审核时间';
COMMENT ON COLUMN agent_versions.published_at IS '发布时间';
COMMENT ON COLUMN agent_versions.user_id IS '用户ID';
COMMENT ON COLUMN agent_versions.created_at IS '创建时间';
COMMENT ON COLUMN agent_versions.updated_at IS '更新时间';
COMMENT ON COLUMN agent_versions.deleted_at IS '软删除标记';

COMMENT ON TABLE agent_workspace IS 'Agent工作区表，记录用户添加到工作区的Agent';
COMMENT ON COLUMN agent_workspace.id IS '工作区记录唯一ID';
COMMENT ON COLUMN agent_workspace.agent_id IS 'Agent ID';
COMMENT ON COLUMN agent_workspace.user_id IS '用户ID';
COMMENT ON COLUMN agent_workspace.created_at IS '创建时间';

-- 验证表创建
SELECT 'v2.1 Agent模块表创建成功！' AS status;

