-- SynapseAI v2.0 会话管理 - 数据库初始化脚本
-- PostgreSQL 版本

-- 会话表
CREATE TABLE IF NOT EXISTS sessions (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    user_id VARCHAR(36) NOT NULL DEFAULT 'default_user',
    description TEXT,
    model VARCHAR(50),
    system_prompt TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_archived BOOLEAN DEFAULT FALSE,
    metadata JSONB
);

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    token_count INTEGER,
    provider VARCHAR(50),
    model VARCHAR(50),
    metadata JSONB,
    CONSTRAINT fk_messages_session FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);

-- 上下文表
CREATE TABLE IF NOT EXISTS context (
    id VARCHAR(36) PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    active_messages JSONB,
    summary TEXT,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_context_session FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE
);

-- 索引
CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id);
CREATE INDEX IF NOT EXISTS idx_sessions_created_at ON sessions(created_at);
CREATE INDEX IF NOT EXISTS idx_sessions_updated_at ON sessions(updated_at);
CREATE INDEX IF NOT EXISTS idx_sessions_is_archived ON sessions(is_archived);

CREATE INDEX IF NOT EXISTS idx_messages_session_id ON messages(session_id);
CREATE INDEX IF NOT EXISTS idx_messages_created_at ON messages(created_at);
CREATE INDEX IF NOT EXISTS idx_messages_role ON messages(role);

CREATE INDEX IF NOT EXISTS idx_context_session_id ON context(session_id);

-- 注释
COMMENT ON TABLE sessions IS '会话表';
COMMENT ON COLUMN sessions.id IS '会话唯一ID';
COMMENT ON COLUMN sessions.title IS '会话标题';
COMMENT ON COLUMN sessions.user_id IS '用户ID';
COMMENT ON COLUMN sessions.description IS '会话描述';
COMMENT ON COLUMN sessions.model IS '使用的模型';
COMMENT ON COLUMN sessions.system_prompt IS '系统提示词';
COMMENT ON COLUMN sessions.created_at IS '创建时间';
COMMENT ON COLUMN sessions.updated_at IS '更新时间';
COMMENT ON COLUMN sessions.is_archived IS '是否归档';
COMMENT ON COLUMN sessions.metadata IS '元数据（JSON）';

COMMENT ON TABLE messages IS '消息表';
COMMENT ON COLUMN messages.id IS '消息唯一ID';
COMMENT ON COLUMN messages.session_id IS '所属会话ID';
COMMENT ON COLUMN messages.role IS '角色：user/assistant/system';
COMMENT ON COLUMN messages.content IS '消息内容';
COMMENT ON COLUMN messages.created_at IS '创建时间';
COMMENT ON COLUMN messages.token_count IS 'Token数量';
COMMENT ON COLUMN messages.provider IS 'LLM提供商';
COMMENT ON COLUMN messages.model IS '使用的模型';
COMMENT ON COLUMN messages.metadata IS '元数据（JSON）';

COMMENT ON TABLE context IS '上下文表';
COMMENT ON COLUMN context.id IS '上下文唯一ID';
COMMENT ON COLUMN context.session_id IS '所属会话ID';
COMMENT ON COLUMN context.active_messages IS '活跃消息列表（JSON）';
COMMENT ON COLUMN context.summary IS '上下文摘要';
COMMENT ON COLUMN context.updated_at IS '更新时间';

-- 验证表创建
SELECT 'v2.0 会话管理表创建成功！' AS status;

