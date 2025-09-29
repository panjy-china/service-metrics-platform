# 三餐打卡与体重反馈分析API文档

## 概述

本API用于分析用户在微信对话中的三餐打卡情况和体重反馈信息，并将分析结果存储到数据库中。

## API端点

### 分析并存储三餐打卡信息

```
POST /api/meal-checkin/analyze-and-store/{date}
```

#### 请求参数

| 参数名 | 类型 | 必需 | 描述 |
|--------|------|------|------|
| date | string | 是 | 开始日期，格式为 yyyy-MM-dd |

#### 请求示例

```bash
curl -X POST "http://localhost:8080/api/meal-checkin/analyze-and-store/2023-10-01"
```

#### 响应格式

```json
{
  "success": true,
  "message": "分析完成",
  "data": {
    "totalConversations": 100,
    "successCount": 95,
    "failedCount": 5,
    "insertedCount": 95
  }
}
```

#### 响应字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| success | boolean | 请求是否成功 |
| message | string | 响应消息 |
| data | object | 分析统计信息 |
| data.totalConversations | integer | 总对话记录数 |
| data.successCount | integer | 分析成功数 |
| data.failedCount | integer | 分析失败数 |
| data.insertedCount | integer | 插入数据库记录数 |

#### 错误响应示例

```json
{
  "success": false,
  "message": "日期格式错误，请使用 yyyy-MM-dd 格式"
}
```

## 工作流程

1. 用户调用API并传入开始日期参数
2. 系统查询指定日期之后的所有对话记录
3. 对每条对话记录逐个进行大模型分析
4. 将分析结果转换为UserMealCheckin对象
5. 批量插入分析结果到数据库
6. 返回分析统计信息

## 数据库表结构

### user_meal_checkin表

| 字段名 | 类型 | 描述 |
|--------|------|------|
| wechat_id | VARCHAR | 用户微信ID |
| checkin_date | DATE | 打卡日期 |
| breakfast_checked | TINYINT | 早餐是否打卡 (0=否, 1=是) |
| lunch_checked | TINYINT | 午餐是否打卡 (0=否, 1=是) |
| dinner_checked | TINYINT | 晚餐是否打卡 (0=否, 1=是) |
| has_weight_feedback | TINYINT | 是否有体重反馈 (0=否, 1=是) |

## 分析逻辑

### 时间段定义

- **早餐**：6:00-11:00
- **午餐**：11:00-13:00
- **晚餐**：17:00-20:00

### 打卡识别规则

在相应时间段内出现以下情况即认定为打卡：
- 图片消息（类型为"Picture"或"WxPic"）
- 饮食反馈文字（包含饮食相关关键词）

### 体重反馈识别规则

- 直接识别包含体重关键词的消息（如"体重"、"斤"、"公斤"、"kg"等）
- 对于早餐时间段发送两次图片的情况，结合上下文判断是否包含体重反馈
- 使用大语言模型进行智能分析，提高识别准确率

## 使用示例

### 成功响应示例

```json
{
  "success": true,
  "message": "分析完成",
  "data": {
    "totalConversations": 50,
    "successCount": 48,
    "failedCount": 2,
    "insertedCount": 48
  }
}
```

### 错误响应示例

```json
{
  "success": false,
  "message": "日期格式错误，请使用 yyyy-MM-dd 格式"
}
```

## 注意事项

1. 为避免API限流，系统在每次分析后会等待1秒钟
2. 如果大模型分析失败，系统会使用备用规则进行分析
3. 分析结果会批量插入数据库以提高性能
4. 系统会记录详细的日志信息，便于问题排查