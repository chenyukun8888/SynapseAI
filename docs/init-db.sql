-- SynapseAI PostgreSQL 数据库初始化脚本
-- 使用方法：psql -U postgres -d synapseai -f docs/init-db.sql

-- LLM 消息表
DROP TABLE IF EXISTS llm_message;

CREATE TABLE llm_message (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(64) NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    model VARCHAR(100),
    tokens_used INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 创建索引
CREATE INDEX idx_llm_message_created_at ON llm_message(created_at);
CREATE INDEX idx_llm_message_role ON llm_message(role);

-- 添加注释
COMMENT ON TABLE llm_message IS 'LLM消息表';
COMMENT ON COLUMN llm_message.id IS '主键ID';
COMMENT ON COLUMN llm_message.message_id IS '消息唯一ID';
COMMENT ON COLUMN llm_message.role IS '角色：user/assistant/system';
COMMENT ON COLUMN llm_message.content IS '消息内容';
COMMENT ON COLUMN llm_message.model IS '使用的模型';
COMMENT ON COLUMN llm_message.tokens_used IS '消耗的token数';
COMMENT ON COLUMN llm_message.created_at IS '创建时间';

-- 验证表创建
SELECT 'LLM 消息表创建成功！' AS status;
SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_name = 'llm_message';
