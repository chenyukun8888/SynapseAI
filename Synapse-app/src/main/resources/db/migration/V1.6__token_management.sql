-- Token管理功能数据库迁移脚本
-- 版本: V1.6
-- 描述: 添加Token管理相关的数据表和字段

-- 创建token_usage_records表
CREATE TABLE IF NOT EXISTS `token_usage_records` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `session_id` varchar(64) NOT NULL COMMENT '会话ID',
    `user_id` varchar(64) NOT NULL COMMENT '用户ID',
    `model_name` varchar(100) NOT NULL COMMENT '模型名称',
    `input_tokens` int DEFAULT '0' COMMENT '输入Token数',
    `output_tokens` int DEFAULT '0' COMMENT '输出Token数',
    `total_tokens` int DEFAULT '0' COMMENT '总Token数',
    `request_time` datetime NOT NULL COMMENT '请求时间',
    `response_time` datetime DEFAULT NULL COMMENT '响应时间',
    `processing_time` bigint DEFAULT NULL COMMENT '处理耗时(毫秒)',
    `success` tinyint(1) DEFAULT '1' COMMENT '是否成功',
    `error_message` text COMMENT '错误信息',
    `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_session_id` (`session_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_model_name` (`model_name`),
    KEY `idx_created_at` (`created_at`),
    KEY `idx_user_created` (`user_id`, `created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Token使用记录表';

-- 为现有的sessions表添加Token管理相关字段
ALTER TABLE `sessions`
    ADD COLUMN `overflow_strategy` varchar(20) DEFAULT 'TRUNCATION' COMMENT 'Token超限处理策略: TRUNCATION, SLIDING_WINDOW, SUMMARY',
    ADD COLUMN `strategy_config` json COMMENT 'Token策略配置参数(JSON格式)',
    ADD COLUMN `summary` text COMMENT '对话摘要内容',
    ADD COLUMN `max_tokens` int DEFAULT '4096' COMMENT '最大Token数',
    ADD COLUMN `summary_threshold` int DEFAULT '20' COMMENT '摘要触发阈值';

-- 为现有的agents表添加Token管理相关字段
ALTER TABLE `agents`
    ADD COLUMN `token_overflow_config` json COMMENT 'Token超限配置(JSON格式)';

-- 为现有的agent_versions表添加Token管理相关字段
ALTER TABLE `agent_versions`
    ADD COLUMN `token_overflow_config` json COMMENT 'Token超限配置(JSON格式)';

-- 创建索引以提高查询性能
CREATE INDEX IF NOT EXISTS `idx_sessions_overflow_strategy` ON `sessions` (`overflow_strategy`);
CREATE INDEX IF NOT EXISTS `idx_sessions_max_tokens` ON `sessions` (`max_tokens`);

-- 初始化默认配置
-- 为现有agents添加默认的token_overflow_config
UPDATE `agents` SET `token_overflow_config` = '{"strategyType": "TRUNCATION"}' WHERE `token_overflow_config` IS NULL;

-- 为现有agent_versions添加默认的token_overflow_config
UPDATE `agent_versions` SET `token_overflow_config` = '{"strategyType": "TRUNCATION"}' WHERE `token_overflow_config` IS NULL;

-- 为现有sessions添加默认的Token管理配置
UPDATE `sessions` SET
    `overflow_strategy` = 'TRUNCATION',
    `strategy_config` = '{"strategyType": "TRUNCATION"}',
    `max_tokens` = 4096,
    `summary_threshold` = 20
WHERE `overflow_strategy` IS NULL;
