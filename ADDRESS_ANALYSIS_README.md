# 地址分析功能说明

## 功能概述

本功能实现了查询7.28后的微信消息数据，使用LLM分析其中的地址信息，并将分析结果存储到ClickHouse数据库中。

## 功能特性

1. **自动查询**: 查询2025年7月28日之后包含地址关键词的微信消息
2. **LLM分析**: 使用大语言模型分析消息中的地址信息
3. **数据存储**: 将分析结果存储到`wechat_message_a_analyze_address`表中
4. **去重处理**: 自动跳过已分析过的消息，避免重复处理
5. **批量处理**: 支持批量处理大量数据，避免API限流

## 数据库表结构

```sql
CREATE TABLE `wechat_message_a_analyze_address` (
  `id` bigint NOT NULL COMMENT '聊天消息的唯一标识符',
  `wechat_id` varchar(255) COLLATE utf8mb4_bin NOT NULL COMMENT '客户微信号',
  `msg_type` int DEFAULT NULL COMMENT '消息类型（例如：文本、图片等）',
  `wechat_time` bigint DEFAULT NULL COMMENT '微信服务器时间戳（毫秒）',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT '聊天消息的内容',
  `address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci COMMENT 'AI分析后的地址'
) ENGINE=ClickHouse DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
```

## 使用方法

### 方法1: 通过测试类运行

```java
@SpringBootTest
public class AddressAnalysisTest {
    
    @Autowired
    private LLMAnalysisService llmAnalysisService;
    
    @Test
    public void testAnalyzeAddresses() {
        String result = llmAnalysisService.analyzeAndStoreAddressesAfterDate728();
        System.out.println(result);
    }
}
```

### 方法2: 通过Runner类运行

```java
@Autowired
private AddressAnalysisRunner addressAnalysisRunner;

// 手动触发分析
addressAnalysisRunner.runAddressAnalysis();
```

### 方法3: 直接调用Service方法

```java
@Autowired
private LLMAnalysisService llmAnalysisService;

// 执行分析
String result = llmAnalysisService.analyzeAndStoreAddressesAfterDate728();
```

## 核心组件

### 1. 实体类
- [`WechatMessageAnalyzeAddress`](src/main/java/org/panjy/servicemetricsplatform/entity/WechatMessageAnalyzeAddress.java): 地址分析结果实体类

### 2. Mapper接口
- [`MessageMapper`](src/main/java/org/panjy/servicemetricsplatform/mapper/MessageMapper.java): 查询原始消息数据
- [`WechatMessageAnalyzeAddressMapper`](src/main/java/org/panjy/servicemetricsplatform/mapper/WechatMessageAnalyzeAddressMapper.java): 操作分析结果数据

### 3. 业务逻辑
- [`LLMAnalysisService`](src/main/java/org/panjy/servicemetricsplatform/service/LLMAnalysisService.java): 核心分析服务
  - `analyzeAndStoreAddressesAfterDate728()`: 主要分析方法
  - `convertToMessageList()`: 数据格式转换
  - `parseAndSaveAnalysisResults()`: 结果解析和存储
  - `extractSimpleAddress()`: 简化地址提取

### 4. 测试和运行
- [`AddressAnalysisServiceTest`](src/test/java/org/panjy/servicemetricsplatform/service/AddressAnalysisServiceTest.java): 单元测试
- [`AddressAnalysisRunner`](src/main/java/org/panjy/servicemetricsplatform/service/AddressAnalysisRunner.java): 运行器

## 配置要求

### 1. API密钥配置
在`application.yml`中配置大语言模型API密钥：
```yaml
dashscope:
  apiKey: \"your-api-key-here\"
```

### 2. 数据库配置
确保ClickHouse数据库连接正常，且已创建目标表。

## 处理流程

1. **查询数据**: 从`wechat_messages_a`表查询7.28后包含地址关键词的消息
2. **格式转换**: 将数据库查询结果转换为LLM分析所需的格式
3. **LLM分析**: 调用大语言模型API分析地址信息
4. **结果解析**: 解析LLM返回的分析结果
5. **数据存储**: 将分析结果批量存储到目标表
6. **去重处理**: 跳过已存在的记录，避免重复分析

## 输出示例

成功执行后会返回类似以下的JSON结果：

```json
{
  \"status\": \"success\",
  \"message\": \"分析完成\",
  \"total_messages\": 150,
  \"saved_count\": 145,
  \"analysis_result\": \"...\"
}
```

## 注意事项

1. **API限流**: 使用小批量处理（默认5条/批次）避免API限流
2. **数据去重**: 系统会自动检查已存在的记录，避免重复处理
3. **错误处理**: 包含完整的异常处理机制，确保部分失败不影响整体流程
4. **内存管理**: 采用分批处理方式，避免大量数据导致内存溢出

## 扩展性

该功能设计具有良好的扩展性：

1. **可配置的日期范围**: 可以轻松修改查询的起始日期
2. **可定制的分析逻辑**: LLM分析提示词可以根据需要调整
3. **支持不同的存储策略**: 可以扩展支持其他数据库或存储方式
4. **模块化设计**: 各个组件职责清晰，便于维护和扩展