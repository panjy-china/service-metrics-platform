# 数据库初始化说明

## 问题描述
地址分析功能出现SQL语法错误：`Unknown column 'id' in 'where clause'`

## 解决方案

### 1. 创建缺失的数据库表

需要在MySQL数据库中执行以下SQL脚本来创建 `wechat_message_a_analyze_address` 表：

```sql
-- 位置：sql/create_wechat_message_analyze_address_table.sql

-- 微信消息地址分析结果表创建脚本
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
```

### 2. 执行方式

#### 方式一：使用MySQL命令行
```bash
mysql -u [username] -p [database_name] < sql/create_wechat_message_analyze_address_table.sql
```

#### 方式二：使用MySQL客户端工具
1. 打开MySQL Workbench、phpMyAdmin或其他MySQL客户端
2. 连接到项目使用的数据库
3. 执行 `sql/create_wechat_message_analyze_address_table.sql` 文件中的SQL语句

#### 方式三：直接复制SQL语句
复制上述CREATE TABLE语句，在MySQL客户端中直接执行

### 3. 验证表创建成功

执行以下SQL语句验证表是否创建成功：

```sql
-- 查看表结构
DESCRIBE `wechat_message_a_analyze_address`;

-- 查看表信息
SHOW CREATE TABLE `wechat_message_a_analyze_address`;
```

### 4. 重新启动应用

表创建成功后，重新启动Spring Boot应用：

```bash
mvn spring-boot:run
```

### 5. 测试地址分析功能

重新测试地址分析接口，确认错误已解决。

## 注意事项

1. **数据库连接配置**：确保 `application.yml` 中的数据库连接配置正确
2. **权限确认**：确保数据库用户有创建表的权限
3. **字符集支持**：表使用utf8mb4字符集，支持emoji和特殊字符
4. **索引优化**：已添加常用查询字段的索引，提高查询性能

## 故障排除

如果仍然出现错误：

1. 检查数据库连接是否正常
2. 确认使用的数据库名称是否正确
3. 验证表是否在正确的数据库中创建
4. 检查应用的数据库配置是否与实际环境一致