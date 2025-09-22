# Service Metrics Platform API 文档

## 概述

Service Metrics Platform 是一个服务指标分析平台，提供微信相关服务的运营指标分析功能。本文档描述了平台提供的REST API接口。

## 基础信息

- **Base URL**: `http://47.100.81.66:9712`
- **API Version**: v1
- **Content-Type**: `application/json`
- **响应格式**: JSON

## 通用响应格式

### 成功响应格式
```json
{
    "success": true,
    "message": "操作成功",
    "data": {...},
    "timestamp": 1234567890
}
```

### 错误响应格式
```json
{
    "success": false,
    "message": "错误描述",
    "errorCode": "ERROR_CODE",
    "timestamp": 1234567890
}
```

## API 接口

---

## 1. 客户分析 API (Client Analysis)

**Base Path**: `/api/client-analysis`

### 1.1 客户年龄分布接口

#### 1.1.1 获取客户年龄分布数据
- **URL**: `GET /api/client-analysis/age-distribution`
- **描述**: 获取客户年龄分布数据
- **参数**: 无

**请求示例**:
```
GET /api/client-analysis/age-distribution
```

**响应示例**:
```json
[
  {
    "ageRange": "18-25",
    "count": 120
  },
  {
    "ageRange": "26-35",
    "count": 200
  }
]
```

---

## 2. 客户信息 API (Client Information)

**Base Path**: `/api/clients`

### 2.1 客户备注信息分析接口

#### 2.1.1 执行客户备注信息分析
- **URL**: `GET /api/clients/valid-demo`
- **描述**: 执行客户备注信息分析，使用大模型分析客户备注信息并提取性别、年龄、身高、体重等信息
- **参数**: 无

**请求示例**:
```
GET /api/clients/valid-demo
```

**响应示例**:
```json
{
  "success": true,
  "message": "分析成功",
  "data": [...],
  "total": 150,
  "statistics": {
    "maleCount": 65,
    "femaleCount": 80,
    "unknownGenderCount": 5,
    "validAgeCount": 140,
    "validHeightCount": 130,
    "validWeightCount": 125
  },
  "updateInfo": {
    "totalClients": 500,
    "updatedCount": 150,
    "successfulBatches": 15,
    "updateRate": "30.00%"
  }
}
```

### 2.2 客户性别分布接口

#### 2.2.1 获取客户性别分布数据
- **URL**: `GET /api/clients/gender-distribution`
- **描述**: 获取客户性别分布数据，包含性别和对应人数
- **参数**: 无

**请求示例**:
```
GET /api/clients/gender-distribution
```

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "gender": "男",
      "count": 120
    },
    {
      "gender": "女",
      "count": 105
    }
  ],
  "total": 2
}
```

### 2.3 客户年龄分布接口

#### 2.3.1 获取客户年龄分布数据
- **URL**: `GET /api/clients/age-distribution`
- **描述**: 获取客户年龄分布数据，包含年龄段和对应人数
- **参数**: 无

**请求示例**:
```
GET /api/clients/age-distribution
```

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "ageRange": "18-25",
      "count": 45
    },
    {
      "ageRange": "26-35",
      "count": 75
    }
  ],
  "total": 2
}
```

### 2.4 客户体重分布接口

#### 2.4.1 获取客户体重分布数据
- **URL**: `GET /api/clients/weight-distribution`
- **描述**: 获取客户体重分布数据，包含体重范围和对应人数
- **参数**: 无

**请求示例**:
```
GET /api/clients/weight-distribution
```

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "weightRange": "50-60kg",
      "count": 35
    },
    {
      "weightRange": "60-70kg",
      "count": 50
    }
  ],
  "total": 2
}
```

### 2.5 客户身高分布接口

#### 2.5.1 获取客户身高分布数据
- **URL**: `GET /api/clients/height-distribution`
- **描述**: 获取客户身高分布数据，包含身高范围和对应人数
- **参数**: 无

**请求示例**:
```
GET /api/clients/height-distribution
```

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": [
    {
      "heightRange": "160-170cm",
      "count": 40
    },
    {
      "heightRange": "170-180cm",
      "count": 65
    }
  ],
  "total": 2
}
```

---

## 3. 策略层指标 API (Strategic Metrics)

**Base Path**: `/api/strategic`

### 3.1 新增用户相关接口

#### 3.1.1 查询日新增用户
- **URL**: `GET /api/strategic/new-users/daily/{date}`
- **描述**: 查询指定日期的新增用户列表
- **路径参数**:
  - `date` (string, required): 查询日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/new-users/daily/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "date": "2024-01-15",
        "newUsers": ["user1", "user2", "user3"],
        "count": 3
    },
    "timestamp": 1705123456789
}
```

#### 3.1.2 查询周新增用户
- **URL**: `GET /api/strategic/new-users/weekly/{date}`
- **描述**: 查询指定周的新增用户列表
- **路径参数**:
  - `date` (string, required): 周内任意一天，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/new-users/weekly/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "weekDate": "2024-01-15",
        "newUsers": ["user1", "user2", "user3", "user4", "user5"],
        "count": 5
    },
    "timestamp": 1705123456789
}
```

#### 3.1.3 查询月新增用户
- **URL**: `GET /api/strategic/new-users/monthly/{date}`
- **描述**: 查询指定月份的新增用户列表
- **路径参数**:
  - `date` (string, required): 月内任意一天，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/new-users/monthly/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "monthDate": "2024-01-15",
        "newUsers": ["user1", "user2", ...],
        "count": 50
    },
    "timestamp": 1705123456789
}
```

### 3.2 新增用户增长率接口

#### 3.2.1 日新增用户增长率
- **URL**: `GET /api/strategic/new-users/daily-growth/{date}`
- **描述**: 查询指定日期的新增用户数及同比前一日增长率
- **路径参数**:
  - `date` (string, required): 查询日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/new-users/daily-growth/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "date": "2024-01-15",
        "currentCount": 10,
        "previousDayCount": 8,
        "growthRate": "25.00",
        "growthRatePercent": "25.00%",
        "previousDayDate": "2024-01-14"
    },
    "timestamp": 1705123456789
}
```

#### 3.2.2 周新增用户增长率
- **URL**: `GET /api/strategic/new-users/weekly-growth/{date}`
- **描述**: 查询指定周的新增用户数及周环比增长率
- **路径参数**:
  - `date` (string, required): 周内任意一天，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/new-users/weekly-growth/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "weekDate": "2024-01-15",
        "currentWeekCount": 50,
        "previousWeekCount": 45,
        "growthRate": "11.11",
        "growthRatePercent": "11.11%",
        "previousWeekDate": "2024-01-08"
    },
    "timestamp": 1705123456789
}
```

#### 3.2.3 月新增用户增长率
- **URL**: `GET /api/strategic/new-users/monthly-growth/{date}`
- **描述**: 查询指定月的新增用户数及月环比增长率
- **路径参数**:
  - `date` (string, required): 月内任意一天，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/new-users/monthly-growth/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "monthDate": "2024-01-15",
        "currentMonthCount": 200,
        "previousMonthCount": 180,
        "growthRate": "11.11",
        "growthRatePercent": "11.11%",
        "previousMonthDate": "2023-12-15"
    },
    "timestamp": 1705123456789
}
```

### 3.3 活跃用户接口

#### 3.3.1 活跃用户数增长率
- **URL**: `GET /api/strategic/active-users-growth/{currentTime}`
- **描述**: 查询指定时间的活跃用户数及同比增长率
- **路径参数**:
  - `currentTime` (string, required): 当前时间，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/active-users-growth/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "currentTime": "2024-01-15",
        "currentActiveUserCount": 1500,
        "previousYearActiveUserCount": 1200,
        "growthRate": "25.00",
        "growthRatePercent": "25.00%",
        "previousYearDate": "2023-01-15"
    },
    "timestamp": 1705123456789
}
```

### 3.4 流失率接口

#### 3.4.1 用户流失率
- **URL**: `GET /api/strategic/churn-rate/{currentTime}`
- **描述**: 计算指定日期的用户流失率
- **路径参数**:
  - `currentTime` (string, required): 指定日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/churn-rate/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "计算成功",
    "data": {
        "currentTime": "2024-01-15",
        "churnRate": "5.25",
        "churnRatePercent": "5.25%"
    },
    "timestamp": 1705123456789
}
```

### 3.5 平均服务时间接口

#### 3.5.1 平均服务时间增长率
- **URL**: `GET /api/strategic/average-service-time-growth/{checkTime}`
- **描述**: 计算指定日期的平均服务时间及同比增长率
- **路径参数**:
  - `checkTime` (string, required): 检查日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/average-service-time-growth/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "计算成功",
    "data": {
        "checkTime": "2024-01-15",
        "currentAverageServiceTimeDays": "2.50",
        "previousYearAverageServiceTimeDays": "3.00",
        "currentAverageServiceTimeFormatted": "2天12小时",
        "previousYearAverageServiceTimeFormatted": "3天",
        "growthRate": "-16.67",
        "growthRatePercent": "-16.67%",
        "previousYearDate": "2023-01-15"
    },
    "timestamp": 1705123456789
}
```

### 3.6 综合概览接口

#### 3.6.1 综合指标概览
- **URL**: `GET /api/strategic/overview-growth/{date}`
- **描述**: 获取包含多个指标的综合概览及同比增长率
- **路径参数**:
  - `date` (string, required): 查询日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/overview-growth/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "获取概览成功",
    "data": {
        "date": "2024-01-15",
        "newUsersCount": 10,
        "newUsersGrowthRate": "25.00",
        "previousDayNewUsersCount": 8,
        "activeUserCount": 1500,
        "activeUsersGrowthRate": "25.00",
        "previousYearActiveUserCount": 1200,
        "averageServiceTimeDays": "2.50",
        "averageServiceTimeFormatted": "2天12小时",
        "serviceTimeGrowthRate": "-16.67",
        "previousYearAverageServiceTimeDays": "3.00",
        "previousYearAverageServiceTimeFormatted": "3天",
        "previousYearDate": "2023-01-15",
        "previousDayDate": "2024-01-14"
    },
    "timestamp": 1705123456789
}
```

---

## 4. 地址分析 API (Address Analysis)

**Base Path**: `/analyze/address`

### 4.1 用户地址分布统计

#### 4.1.1 获取全国各地用户分布
- **URL**: `GET /analyze/address/distribution`
- **描述**: 获取全国各地用户分布统计，基于用户地址数据进行省份/地区分组统计
- **参数**: 无

**请求示例**:
```
GET /analyze/address/distribution
```

**响应示例**:
```json
{
    "success": true,
    "message": "用户地址分布查询成功",
    "data": [
        {
            "province": "北京",
            "count": 128
        },
        {
            "province": "上海",
            "count": 95
        },
        {
            "province": "广东",
            "count": 87
        },
        {
            "province": "内蒙古",
            "count": 23
        },
        {
            "province": "黑龙江",
            "count": 15
        }
    ],
    "totalUsers": 348,
    "provinceCount": 5,
    "queryTime": "2024-01-15 14:30:00"
}
```

**说明**:
- 数据基于 `wechat_message_a_analyze_address` 表中的地址信息
- 排除北京大兴地区的数据
- 每个用户取最新的地址记录
- 省份提取规则：
  - 默认取地址前两个字符作为省份标识
  - 特殊处理："内蒙"开头的识别为"内蒙古"
  - 特殊处理："黑龙"开头的识别为"黑龙江"
- 结果按用户数量降序排列

### 4.2 按日期地址分析

#### 4.2.1 分析指定日期的地址信息
- **URL**: `GET /analyze/address/{date}`
- **描述**: 分析指定日期包含地址关键词的消息，使用LLM提取标准化地址信息
- **路径参数**:
  - `date` (string, required): 查询日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /analyze/address/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "地址分析处理完成",
    "data": [
        {
            "sender": "user123",
            "type": "AddressAnalysis",
            "message": "地址分析结果: 北京市朝阳区 (原文: 我在朝阳区这边)",
            "chatTime": "2024-01-15T10:30:00"
        }
    ],
    "totalFound": 50,
    "processedCount": 50,
    "extractedAddresses": 35,
    "savedToDatabase": 35,
    "batchCount": 5,
    "queryDate": "2024-01-15"
}
```

**说明**:
- 使用分批处理机制，每批处理10条消息
- 包含重试机制，最大重试3次
- 自动保存分析结果到数据库
- 支持去重，避免重复分析同一条消息

---

## 5. 订单指标 API (Order Metrics)

**Base Path**: `/api/order-metrics`

### 5.1 人均订单数接口

#### 5.1.1 指定月份人均订单数
- **URL**: `GET /api/order-metrics/avg-orders-per-customer/{yearMonth}`
- **描述**: 获取指定月份人均成交订单数
- **路径参数**:
  - `yearMonth` (string, required): 指定月份，格式: `YYYY-MM`

**请求示例**:
```
GET /api/order-metrics/avg-orders-per-customer/2024-01
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "yearMonth": "2024-01",
    "avgOrdersPerCustomer": "2.50",
    "description": "指定月份人均成交订单数"
}
```

#### 5.1.2 当月人均订单数
- **URL**: `GET /api/order-metrics/avg-orders-per-customer`
- **描述**: 获取当月人均成交订单数（向后兼容接口）

**请求示例**:
```
GET /api/order-metrics/avg-orders-per-customer
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "avgOrdersPerCustomer": "2.50",
    "description": "当月人均成交订单数"
}
```

### 5.2 人均销售额接口

#### 5.2.1 指定月份人均销售额
- **URL**: `GET /api/order-metrics/avg-sales-per-customer/{yearMonth}`
- **描述**: 获取指定月份人均成交销售额
- **路径参数**:
  - `yearMonth` (string, required): 指定月份，格式: `YYYY-MM`

**请求示例**:
```
GET /api/order-metrics/avg-sales-per-customer/2024-01
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "yearMonth": "2024-01",
    "avgSalesPerCustomer": "1250.00",
    "description": "指定月份人均成交销售额"
}
```

#### 5.2.2 当月人均销售额
- **URL**: `GET /api/order-metrics/avg-sales-per-customer`
- **描述**: 获取当月人均成交销售额（向后兼容接口）

**请求示例**:
```
GET /api/order-metrics/avg-sales-per-customer
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "avgSalesPerCustomer": "1250.00",
    "description": "当月人均成交销售额"
}
```

### 5.3 综合统计接口

#### 5.3.1 指定月份综合统计
- **URL**: `GET /api/order-metrics/monthly-stats/{yearMonth}`
- **描述**: 获取指定月份综合统计数据，包含同比增长率
- **路径参数**:
  - `yearMonth` (string, required): 指定月份，格式: `YYYY-MM`

**请求示例**:
```
GET /api/order-metrics/monthly-stats/2024-01
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "period": "2024-01",
        "previousPeriod": "2023-12",
        "avgOrdersPerCustomer": "2.50",
        "prevAvgOrdersPerCustomer": "2.20",
        "avgOrdersGrowthRate": "13.64",
        "avgOrdersGrowthRatePercent": "13.64%",
        "avgSalesPerCustomer": "1250.00",
        "prevAvgSalesPerCustomer": "1100.00",
        "avgSalesGrowthRate": "13.64",
        "avgSalesGrowthRatePercent": "13.64%"
    },
    "description": "指定月份订单综合统计数据（包含同比增长）"
}
```

#### 5.3.2 当月综合统计
- **URL**: `GET /api/order-metrics/current-month-stats`
- **描述**: 获取当月综合统计数据，包含同比增长率（向后兼容接口）

**请求示例**:
```
GET /api/order-metrics/current-month-stats
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "period": "2024-01",
        "previousPeriod": "2023-12",
        "avgOrdersPerCustomer": "2.50",
        "prevAvgOrdersPerCustomer": "2.20",
        "avgOrdersGrowthRate": "13.64",
        "avgOrdersGrowthRatePercent": "13.64%",
        "avgSalesPerCustomer": "1250.00",
        "prevAvgSalesPerCustomer": "1100.00",
        "avgSalesGrowthRate": "13.64",
        "avgSalesGrowthRatePercent": "13.64%"
    },
    "description": "当月订单综合统计数据（包含同比增长）"
}
```

### 5.4 概览接口

#### 5.4.1 订单指标概览
- **URL**: `GET /api/order-metrics/overview`
- **描述**: 获取订单相关核心指标的概览，包含同比增长情况

**请求示例**:
```
GET /api/order-metrics/overview
```

**响应示例**:
```json
{
    "success": true,
    "message": "获取概览成功",
    "overview": {
        "period": "2024-01",
        "previousPeriod": "2023-12",
        "avgOrdersPerCustomer": "2.50",
        "avgOrdersGrowthRate": "13.64",
        "avgOrdersGrowthRatePercent": "13.64%",
        "avgSalesPerCustomer": "1250.00",
        "avgSalesGrowthRate": "13.64",
        "avgSalesGrowthRatePercent": "13.64%",
        "calculationMethod": "基于ACCOUNT_NUMBER常量计算人均指标，同比上月增长率"
    },
    "description": "订单核心指标概览（包含同比增长）"
}
```

### 5.5 健康检查接口

#### 5.5.1 服务健康检查
- **URL**: `GET /api/order-metrics/health`
- **描述**: 验证订单服务的可用性

**请求示例**:
```
GET /api/order-metrics/health
```

**响应示例**:
```json
{
    "success": true,
    "message": "订单指标服务运行正常",
    "service": "OrderMetricsController",
    "status": "healthy",
    "timestamp": 1705123456789
}
```

### 5.6 平均成交天数接口

#### 5.6.1 获取指定客户的服务时间
- **URL**: `GET /api/order-metrics/service-time/client/{clientId}`
- **描述**: 获取指定客户的服务时间（最晚下单时间 - 最早下单时间）
- **路径参数**:
  - `clientId` (string, required): 客户ID

**请求示例**:
```
GET /api/order-metrics/service-time/client/CUST001
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "clientId": "CUST001",
    "serviceTimeInDays": 15.5,
    "description": "客户使用服务的时间（最晚下单时间 - 最早下单时间）"
}
```

#### 5.6.2 获取所有客户的平均成交天数
- **URL**: `GET /api/order-metrics/average-service-time`
- **描述**: 获取所有客户的平均成交天数（最晚下单时间 - 最早下单时间）

**请求示例**:
```
GET /api/order-metrics/average-service-time
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "averageServiceTimeInDays": 22.3,
    "description": "所有客户平均成交天数（最晚下单时间 - 最早下单时间）"
}
```

### 5.7 客户分析接口

#### 5.7.1 获取指定日期之后首次下单的客户列表
- **URL**: `GET /api/order-metrics/new-clients-after/{dateStr}`
- **描述**: 获取在指定日期之后首次下单的客户列表
- **路径参数**:
  - `dateStr` (string, required): 指定日期，格式: `yyyy-MM-dd HH:mm:ss`

**请求示例**:
```
GET /api/order-metrics/new-clients-after/2024-01-01 00:00:00
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "date": "2024-01-01 00:00:00",
    "newClientCount": 15,
    "newClientIds": ["CUST001", "CUST002", ...],
    "description": "在指定日期之后首次下单的客户列表"
}
```

#### 5.7.2 计算指定日期之后用户的十日成交转换率
- **URL**: `GET /api/order-metrics/ten-day-conversion-rate/{dateStr}`
- **描述**: 计算指定日期之后用户的十日成交转换率（服务时间在2天到10天之间的用户数 / 指定日期之后用户数）
- **路径参数**:
  - `dateStr` (string, required): 指定日期，格式: `yyyy-MM-dd HH:mm:ss`

**请求示例**:
```
GET /api/order-metrics/ten-day-conversion-rate/2024-01-01 00:00:00
```

**响应示例**:
```json
{
    "success": true,
    "message": "计算成功",
    "date": "2024-01-01 00:00:00",
    "conversionRate": 0.65,
    "conversionRatePercentage": "65.00%",
    "description": "十日成交转换率（服务时间在2天到10天之间的用户数 / 指定日期之后用户数）"
}
```

#### 5.7.3 计算指定日期之后用户的十五日成交转换率
- **URL**: `GET /api/order-metrics/fifteen-day-conversion-rate/{dateStr}`
- **描述**: 计算指定日期之后用户的十五日成交转换率（服务时间在2天到15天之间的用户数 / 指定日期之后用户数）
- **路径参数**:
  - `dateStr` (string, required): 指定日期，格式: `yyyy-MM-dd HH:mm:ss`

**请求示例**:
```
GET /api/order-metrics/fifteen-day-conversion-rate/2024-01-01 00:00:00
```

**响应示例**:
```json
{
    "success": true,
    "message": "计算成功",
    "date": "2024-01-01 00:00:00",
    "conversionRate": 0.78,
    "conversionRatePercentage": "78.00%",
    "description": "十五日成交转换率（服务时间在2天到15天之间的用户数 / 指定日期之后用户数）"
}
```

---

## 错误代码说明

| 错误代码 | 描述 | HTTP状态码 |
|---------|------|-----------|
| INVALID_DATE | 日期参数无效 | 400 |
| INVALID_TIME | 时间参数无效 | 400 |
| QUERY_ERROR | 查询失败 | 500 |
| CALCULATION_ERROR | 计算失败 | 500 |
| OVERVIEW_ERROR | 获取概览失败 | 500 |
| SERVICE_ERROR | 服务异常 | 503 |
| PROCESSING_ERROR | 处理失败 | 500 |
| NO_DATA_FOUND | 未找到数据 | 404 |

## 注意事项

1. **日期格式**: 所有日期参数必须严格按照 `yyyy-MM-dd` 格式提供
2. **月份格式**: 月份参数必须按照 `YYYY-MM` 格式提供
3. **时间格式**: 时间参数必须按照 `yyyy-MM-dd HH:mm:ss` 格式提供
4. **数值精度**: 所有数值计算结果保留2位小数
5. **增长率计算**: 增长率 = (当前值 - 上期值) / 上期值 × 100%
6. **时区**: 所有时间均基于服务器本地时区
7. **数据范围**: 查询范围依赖于数据库中的实际数据
8. **批量处理**: 地址分析接口使用批量处理机制，避免超时和内存问题

## 使用示例

### 获取综合业务概览
```bash
# 获取策略指标概览
curl -X GET "http://localhost:8080/api/strategic/overview-growth/2024-01-15"

# 获取订单指标概览
curl -X GET "http://localhost:8080/api/order-metrics/overview"
```

### 服务时间接口
```bash
# 处理指定日期之后的所有记录，计算平均服务时间并写入到tbl_ServerTime表中
curl -X POST "http://localhost:8080/api/server-time/process-after/2024-01-01"

# 查询指定日期之后的所有服务时间记录
curl -X GET "http://localhost:8080/api/server-time/after/2024-01-01"
```

## 新增服务时间接口说明

### 6.1 处理指定日期之后的服务时间记录
- **URL**: `POST /api/server-time/process-after/{dateStr}`
- **描述**: 处理指定日期之后的所有记录：查询指定日期后出现的用户id，之后查询该用户id的最早一条记录以及最晚一条记录，将两者之差作为服务时间
- **路径参数**:
  - `dateStr` (string, required): 指定日期，格式: `yyyy-MM-dd`

**请求示例**:
```
POST /api/server-time/process-after/2024-01-01
```

**响应示例**:
```json
{
    "success": true,
    "message": "处理成功",
    "date": "2024-01-01",
    "description": "已将指定日期之后的所有记录的每个用户服务时间写入到tbl_ServerTime表中"
}
```

### 6.2 查询指定日期之后的服务时间记录
- **URL**: `GET /api/server-time/after/{dateStr}`
- **描述**: 查询指定日期之后的所有服务时间记录
- **路径参数**:
  - `dateStr` (string, required): 指定日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/server-time/after/2024-01-01
```

**响应示例**:
``json
{
    "success": true,
    "message": "查询成功",
    "date": "2024-01-01",
    "recordCount": 15,
    "serverTimes": [
        {
            "colCltID": "CUST001",
            "colSerTi": 86400,
            "createTime": "2024-01-01T10:00:00",
            "updateTime": "2024-01-01T10:00:00"
        },
        // ... 更多记录
    ],
    "description": "指定日期之后的所有服务时间记录"
}
```

### 6.3 查询所有客户的服务时间记录
- **URL**: `GET /api/server-time/all`
- **描述**: 查询所有客户的服务时间记录

**请求示例**:
```
GET /api/server-time/all
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "recordCount": 150,
    "serverTimes": [
        {
            "colCltID": "CUST001",
            "colSerTi": 86400,
            "createTime": "2024-01-01T10:00:00",
            "updateTime": "2024-01-01T10:00:00"
        },
        // ... 更多记录
    ],
    "description": "所有客户的服务时间记录"
}
```

### 监控业务增长
```
# 监控日新增用户增长
curl -X GET "http://localhost:8080/api/strategic/new-users/daily-growth/2024-01-15"

# 监控月度订单指标增长
curl -X GET "http://localhost:8080/api/order-metrics/monthly-stats/2024-01"
```

### 客户分析
```
# 获取客户年龄分布
curl -X GET "http://localhost:8080/api/clients/age-distribution"

# 获取客户性别分布
curl -X GET "http://localhost:8080/api/clients/gender-distribution"

# 执行客户备注信息分析
curl -X GET "http://localhost:8080/api/clients/valid-demo"
```

### 地址分析
```
# 获取全国各地用户分布
curl -X GET "http://localhost:8080/analyze/address/distribution"

# 分析指定日期的地址信息
curl -X GET "http://localhost:8080/analyze/address/2024-01-15"
```

---

*最后更新时间: 2025-09-22*