-- 微信消息地址分析结果表创建脚本
-- 表名：wechat_message_a_analyze_address

-- 先检查表是否存在，如果存在则显示当前结构
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT,
    COLUMN_COMMENT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = DATABASE() 
  AND TABLE_NAME = 'wechat_message_a_analyze_address'
ORDER BY ORDINAL_POSITION;

-- 如果表不存在，创建新表
CREATE TABLE IF NOT EXISTS `wechat_message_a_analyze_address` (
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

-- 如果表已存在但缺少id列，添加id列
-- 注意：这个操作需要根据实际情况谨慎执行
SET @sql = (
    SELECT IF(
        (SELECT COUNT(*) 
         FROM INFORMATION_SCHEMA.COLUMNS 
         WHERE TABLE_SCHEMA = DATABASE() 
           AND TABLE_NAME = 'wechat_message_a_analyze_address' 
           AND COLUMN_NAME = 'id') = 0,
        'ALTER TABLE `wechat_message_a_analyze_address` ADD COLUMN `id` BIGINT NOT NULL PRIMARY KEY FIRST COMMENT "聊天消息的唯一标识符";',
        'SELECT "id列已存在" AS status;'
    )
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 检查表是否创建成功
SHOW CREATE TABLE `wechat_message_a_analyze_address`;

-- 查看表结构
DESCRIBE `wechat_message_a_analyze_address`;