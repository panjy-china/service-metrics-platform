-- 修复 wechat_message_a_analyze_address 表结构
-- 添加缺失的 id 列

-- 检查表是否存在
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, IS_NULLABLE, COLUMN_DEFAULT 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'ruoyi-vue-pro' 
  AND TABLE_NAME = 'wechat_message_a_analyze_address'
ORDER BY ORDINAL_POSITION;

-- 如果表存在但缺少 id 列，执行以下 ALTER 语句
ALTER TABLE `wechat_message_a_analyze_address` 
ADD COLUMN `id` BIGINT NOT NULL PRIMARY KEY FIRST COMMENT '聊天消息的唯一标识符';

-- 如果表已有数据但没有主键，可能需要先添加一个自增的临时列，然后重新设计
-- 查看表当前结构
DESCRIBE `wechat_message_a_analyze_address`;

-- 如果需要完全重建表（备份数据后执行）
-- 第一步：备份现有数据
CREATE TABLE `wechat_message_a_analyze_address_backup` AS 
SELECT * FROM `wechat_message_a_analyze_address`;

-- 第二步：删除原表
DROP TABLE `wechat_message_a_analyze_address`;

-- 第三步：重建表结构
CREATE TABLE `wechat_message_a_analyze_address` (
    `id` BIGINT NOT NULL COMMENT '聊天消息的唯一标识符',
    `wechat_id` VARCHAR(64) NOT NULL COMMENT '客户微信号',
    `msg_type` INT NOT NULL DEFAULT 1 COMMENT '消息类型（1:文本，3:图片，34:语音，49:小程序等）',
    `wechat_time` BIGINT NOT NULL COMMENT '微信服务器时间戳（毫秒）',
    `content` LONGTEXT COMMENT '聊天消息的内容',
    `address` LONGTEXT COMMENT 'AI分析后的地址信息',
    `created_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_wechat_id` (`wechat_id`),
    INDEX `idx_wechat_time` (`wechat_time`),
    INDEX `idx_created_time` (`created_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='微信消息地址分析结果表';

-- 第四步：如果有备份数据，恢复数据（根据实际字段调整）
-- INSERT INTO `wechat_message_a_analyze_address` 
-- (wechat_id, msg_type, wechat_time, content, address)
-- SELECT wechat_id, msg_type, wechat_time, content, address 
-- FROM `wechat_message_a_analyze_address_backup`;

-- 验证表结构
DESCRIBE `wechat_message_a_analyze_address`;
SHOW CREATE TABLE `wechat_message_a_analyze_address`;