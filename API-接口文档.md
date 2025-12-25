# 服务指标平台 API 接口文档

## 1. 概述

服务指标平台是一个用于分析和展示服务指标的系统，主要功能包括客户信息分析、地址分析、数据看板等。系统使用Spring Boot框架开发，通过RESTful API提供服务。

## 2. 基础信息

- **服务端口**: 8081
- **基础URL**: http://47.100.81.66:9712
- **数据源**: ClickHouse数据库
- **返回格式**: JSON

## 3. API 接口列表

### 3.1 客户信息接口

#### 3.1.1 获取客户分析结果
- **接口地址**: `GET /api/clients/valid-demo`
- **功能描述**: 执行客户备注信息分析，返回分析后的客户列表和统计信息
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "分析成功",
  "data": [
    {
      "colGender": "男",
      "colAge": 25,
      "colHeight": 175,
      "colWeight": 70
    }
  ],
  "total": 100,
  "statistics": {
    "maleCount": 60,
    "femaleCount": 40,
    "validAgeCount": 90,
    "validHeightCount": 85,
    "validWeightCount": 80
  },
  "updateInfo": {
    "totalClients": 100,
    "updatedCount": 80,
    "successfulBatches": 5,
    "updateRate": "80.00%"
  }
}
```

#### 3.1.2 获取客户性别分布
- **接口地址**: `GET /api/clients/gender-distribution`
- **功能描述**: 获取客户性别分布数据
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {"gender": "男", "count": 60},
    {"gender": "女", "count": 40}
  ],
  "total": 2
}
```

#### 3.1.3 获取客户年龄分布
- **接口地址**: `GET /api/clients/age-distribution`
- **功能描述**: 获取客户年龄分布数据
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {"ageRange": "18-25", "count": 30},
    {"ageRange": "26-35", "count": 50}
  ],
  "total": 2
}
```

#### 3.1.4 获取客户体重分布
- **接口地址**: `GET /api/clients/weight-distribution`
- **功能描述**: 获取客户体重分布数据
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {"weightRange": "50-60", "count": 20},
    {"weightRange": "61-70", "count": 40}
  ],
  "total": 2
}
```

#### 3.1.5 获取客户身高分布
- **接口地址**: `GET /api/clients/height-distribution`
- **功能描述**: 获取客户身高分布数据
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {"heightRange": "160-170", "count": 35},
    {"heightRange": "171-180", "count": 45}
  ],
  "total": 2
}
```

### 3.2 地址分析接口

#### 3.2.1 获取用户地址分布
- **接口地址**: `GET /analyze/address/distribution`
- **功能描述**: 获取全国各地用户分布数据
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "用户地址分布查询成功",
  "data": [
    {"province": "北京", "count": 150},
    {"province": "上海", "count": 120}
  ],
  "totalUsers": 500,
  "provinceCount": 25,
  "queryTime": "2024-12-24 10:30:00"
}
```

#### 3.2.2 按日期分析地址
- **接口地址**: `GET /analyze/address/{date}`
- **功能描述**: 根据指定日期分析包含地址关键词的消息
- **路径参数**: 
  - `date`: 日期，格式为 `yyyy-MM-dd`
- **返回示例**:
```json
{
  "success": true,
  "message": "地址分析处理完成",
  "data": [],
  "totalFound": 50,
  "processedCount": 45,
  "extractedAddresses": 30,
  "savedToDatabase": 25,
  "batchCount": 5,
  "queryDate": "2024-12-24"
}
```

### 3.3 综合指标接口

#### 3.3.1 获取综合指标数据
- **接口地址**: `GET /api/comprehensive/metrics/{date}`
- **功能描述**: 获取综合指标数据，包括性别、年龄、体重、地区分布，本日、本周、本月新增及其环比，留存率、转化率等
- **路径参数**: 
  - `date`: 日期，格式为 `yyyy-MM-dd`
- **返回示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "genderDistribution": [...],
    "ageDistribution": [...],
    "weightDistribution": [...],
    "regionDistribution": [...],
    "dailyNewUsers": {...},
    "weeklyNewUsers": {...},
    "monthlyNewUsers": {...},
    "threeDayRetentionRate": {...},
    "sevenDayRetentionRate": {...},
    "tenDayRetentionRate": {...},
    "tenDayConversionRate": 85.50,
    "fifteenDayConversionRate": 90.25,
    "currentMonthAvgOrdersPerCustomer": 12.5,
    "currentMonthAvgSalesPerCustomer": 2500.75,
    "avgOrdersGrowthRate": 5.25,
    "avgSalesGrowthRate": 3.75
  }
}
```

### 3.4 数据看板接口

#### 3.4.1 获取所有数据看板指标
- **接口地址**: `GET /api/data-dashboard/metrics`
- **功能描述**: 获取数据看板所有指标数据，包括首电、用户基础资料、三餐打卡、饮食指导、通话次数等
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "firstCallAverageDuration": "3.25",
    "firstCallQualifiedRate": "85.50",
    "userBasicInfoSubmissionRate": "78.25",
    "allUsersMealCheckinRate": "65.75",
    "weightFeedbackCompletionRate": "45.20",
    "dietaryGuidanceReachRate": "92.30",
    "fourCallComplianceRate": "75.50",
    "sixCallComplianceRate": "68.25",
    "firstCallAverageCompletionTime": "12.50",
    "traditionalChineseMedicineGuidanceCompletionRate": "55.75",
    "tonguePhotoSubmissionRate": "35.20",
    "bodyTypePhotoSubmissionRate": "42.75",
    "firstThreeDaysCompletionRate": "78.50",
    "fourToSixDaysCompletionRate": "65.25",
    "sevenToTenDaysCompletionRate": "58.75",
    "pushOrderConversionRate": "25.50",
    "orderRetentionRate": "45.25"
  }
}
```

#### 3.4.2 按日期获取数据看板指标
- **接口地址**: `GET /api/data-dashboard/metrics/{date}`
- **功能描述**: 根据指定日期获取数据看板指标数据
- **路径参数**: 
  - `date`: 日期，格式为 `yyyy-MM-dd`
- **返回示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "firstCallAverageDuration": "3.25",
    "firstCallQualifiedRate": "85.50",
    "firstCallAverageDurationGrowth": "2.50",
    "firstCallQualifiedRateGrowth": "1.75",
    "userBasicInfoSubmissionRate": "78.25",
    "userBasicInfoSubmissionRateGrowth": "3.25",
    "allUsersMealCheckinRate": "65.75",
    "allUsersMealCheckinRateGrowth": "-1.25",
    "weightFeedbackCompletionRate": "45.20",
    "weightFeedbackCompletionRateGrowth": "2.10",
    "dietaryGuidanceReachRate": "92.30",
    "dietaryGuidanceReachRateGrowth": "0.75",
    "fourCallComplianceRate": "75.50",
    "fourCallComplianceRateGrowth": "1.50",
    "sixCallComplianceRate": "68.25",
    "sixCallComplianceRateGrowth": "-0.25"
  }
}
```

### 3.5 健康画像分析任务接口

#### 3.5.1 手动执行健康画像分析任务
- **接口地址**: `POST /api/health-profile-analysis-task/execute`
- **功能描述**: 手动执行健康画像分析任务，分析昨日的对话记录并提取健康相关标签
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "健康画像分析任务执行成功",
  "data": null
}
```

#### 3.5.2 获取健康画像分析任务状态
- **接口地址**: `GET /api/health-profile-analysis-task/status`
- **功能描述**: 获取健康画像分析定时任务的状态信息
- **请求参数**: 无
- **返回示例**:
```json
{
  "success": true,
  "message": "健康画像分析定时任务服务正常",
  "data": null
}
```

## 4. 错误码说明

| 错误码 | 说明 |
|--------|------|
| INVALID_DATE | 日期参数不能为空 |
| QUERY_ERROR | 查询失败 |
| PROCESSING_ERROR | 处理失败 |
| HEALTH_PROFILE_ANALYSIS_ERROR | 健康画像分析任务执行失败 |

## 5. 返回格式说明

所有API接口返回统一的JSON格式：

```json
{
  "success": true/false,    // 请求是否成功
  "message": "响应消息",     // 响应消息
  "data": {},              // 响应数据（可能为null或数组/对象）
  "timestamp": 1234567890  // 时间戳（部分接口）
}
```

## 6. 注意事项

1. 所有日期格式均为 `yyyy-MM-dd`
2. 百分比数据返回为保留两位小数的字符串格式（不带%符号）
3. 部分接口使用大模型进行分析，可能需要较长时间处理
4. 数据来源为ClickHouse数据库，确保数据库连接正常
5. 某些接口需要配置DashScope API Key才能正常工作
6. 健康画像分析任务接口依赖于对话记录和大模型API，确保相关服务正常运行

## 7. 定时任务

UnifiedScheduledTask
```
每天早上6点执行，按顺序执行所有定时任务
1. 处理昨天新增的地址信息并匹配用户
2. 调用地址分析接口
3. 执行饮食指导分析任务
4. 执行基于日期的对话分析任务（替换原有的舌苔体型分析）
5. 执行三餐打卡分析任务
6. 执行服务时间处理任务
7. 执行用户首次反馈统计任务
8. 执行用户首次聊天记录统计任务
```

HealthProfileAnalysisScheduledTask
```
每日早上三点执行，分析当天聊天记录表（wechat_message）中的所有用户聊天记录，检查是否存在慢病标签，存入标签表（wechat_user_label）
```