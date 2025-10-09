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
        "currentDayCount": 10,
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
        "currentMonthCount": 150,
        "previousMonthCount": 120,
        "growthRate": "25.00",
        "growthRatePercent": "25.00%",
        "previousMonthDate": "2023-12-15"
    },
    "timestamp": 1705123456789
}
```

### 3.3 留存率接口

#### 3.3.1 用户留存率
- **URL**: `GET /api/strategic/retention-rate/{days}/{currentTime}`
- **描述**: 计算指定日期的用户留存率
- **路径参数**:
  - `days` (integer, required): 留存天数，可选值: 3, 7, 10
  - `currentTime` (string, required): 指定日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/strategic/retention-rate/7/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "计算成功",
    "data": {
        "currentTime": "2024-01-15",
        "days": 7,
        "retentionRate": "65.25",
        "retentionRatePercent": "65.25%"
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

### 5.5 服务时间接口

#### 5.5.1 获取指定客户的成交天数
- **URL**: `GET /api/order-metrics/service-time/{clientId}`
- **描述**: 获取指定客户的成交天数（最晚下单时间 - 最早下单时间）

**请求示例**:
```
GET /api/order-metrics/service-time/CUST001
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

#### 5.5.2 获取所有客户的平均成交天数
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

### 5.6 客户分析接口

#### 5.6.1 获取指定日期之后首次下单的客户列表
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

#### 5.6.2 计算指定日期之后用户的十日成交转换率
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

#### 5.6.3 计算指定日期之后用户的十五日成交转换率
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
    "conversionRate": 0.45,
    "conversionRatePercentage": "45.00%",
    "description": "十五日成交转换率（服务时间在2天到15天之间的用户数 / 指定日期之后用户数）"
}
```

---

## 6. 综合指标 API (Comprehensive Metrics)

**Base Path**: `/api/comprehensive`

### 6.1 综合指标数据接口

#### 6.1.1 获取综合指标数据
- **URL**: `GET /api/comprehensive/metrics/{date}`
- **描述**: 获取包含性别、年龄、体重、地区分布，本日、本周、本月新增及其环比，三日留存率、七日留存率、十日留存率、七日流失率、十日转化率、十五日转化率，人均成交客户数及成交销售额及其增长比例，平均服务天数及成交天数以及其增长比例的综合数据
- **路径参数**:
  - `date` (string, required): 查询日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/comprehensive/metrics/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "genderDistribution": [
            {
                "gender": "男",
                "count": 120
            },
            {
                "gender": "女",
                "count": 105
            }
        ],
        "ageDistribution": [
            {
                "ageGroup": "18-24岁",
                "count": 45
            },
            {
                "ageGroup": "25-34岁",
                "count": 75
            }
        ],
        "weightDistribution": [
            {
                "weightGroup": "50-60kg",
                "count": 35
            },
            {
                "weightGroup": "60-70kg",
                "count": 50
            }
        ],
        "regionDistribution": [
            {
                "province": "北京",
                "count": 128
            },
            {
                "province": "上海",
                "count": 95
            }
        ],
        "dailyNewUsers": {
            "currentValue": 10,
            "previousDayValue": 8,
            "growthRate": "25.00",
            "date": "2024-01-15",
            "previousDayDate": "2024-01-14"
        },
        "weeklyNewUsers": {
            "currentValue": 50,
            "previousWeekValue": 45,
            "growthRate": "11.11",
            "weekDate": "2024-01-15",
            "previousWeekDate": "2024-01-08"
        },
        "monthlyNewUsers": {
            "currentValue": 150,
            "previousMonthValue": 120,
            "growthRate": "25.00",
            "monthDate": "2024-01-15",
            "previousMonthDate": "2023-12-15"
        },
        "threeDayRetentionRate": {
            "currentValue": "45.25",
            "previousYearValue": "42.10",
            "growthRate": "7.48",
            "days": 3,
            "currentDate": "2024-01-15",
            "previousYearDate": "2023-01-15"
        },
        "sevenDayRetentionRate": {
            "currentValue": "65.25",
            "previousYearValue": "62.10",
            "growthRate": "5.09",
            "days": 7,
            "currentDate": "2024-01-15",
            "previousYearDate": "2023-01-15"
        },
        "tenDayRetentionRate": {
            "currentValue": "75.25",
            "previousYearValue": "72.10",
            "growthRate": "4.37",
            "days": 10,
            "currentDate": "2024-01-15",
            "previousYearDate": "2023-01-15"
        },
        "sevenDayChurnRate": {
            "currentValue": "5.25",
            "previousYearValue": "6.10",
            "growthRate": "-13.93",
            "days": 7,
            "currentDate": "2024-01-15",
            "previousYearDate": "2023-01-15"
        },
        "tenDayConversionRate": "0.6500",
        "fifteenDayConversionRate": "0.4500",
        "currentMonthAvgOrdersPerCustomer": "2.50",
        "currentMonthAvgSalesPerCustomer": "1250.00",
        "avgOrdersGrowthRate": "13.64",
        "avgSalesGrowthRate": "13.64",
        "averageServiceTime": {
            "currentValue": "2.50",
            "previousYearValue": "3.00",
            "growthRate": "-16.67",
            "currentDate": "2024-01-15",
            "previousYearDate": "2023-01-15"
        },
        // 平均成交时间
        "averageDealTime": {
            "currentValue": "15.50"
        }
    },
    "timestamp": 1705123456789
}
```

---

## 7. 用户指导统计 API (User Guidance Statistics)

**Base Path**: `/api/user-guidance`

### 7.1 饮食指导分析接口

#### 7.1.1 分析对话中的饮食指导情况
- **URL**: `POST /api/user-guidance/analyze`
- **描述**: 分析对话中的饮食指导情况，当客服对用户进行饮食信息的指导时看作一次指导次数，若该指导字数超过15字则认为是一次个性化饮食指导
- **请求参数**:
  - `conversation` (Conversation object, required): 对话记录对象

**请求示例**:
```json
POST /api/user-guidance/analyze
Content-Type: application/json

{
  "wechatId": "user123",
  "date": "2024-01-15T10:30:00",
  "messages": [
    {
      "sender": "客服",
      "message": "建议您控制热量摄入，多吃蔬菜水果",
      "type": "Text",
      "chatTime": "2024-01-15T10:30:00"
    },
    {
      "sender": "用户",
      "message": "好的，谢谢",
      "type": "Text",
      "chatTime": "2024-01-15T10:31:00"
    }
  ]
}
```

**响应示例**:
```json
{
  "createTime": "2024-01-15",
  "wechatId": "user123",
  "guidanceCount": 1,
  "personalizedGuidanceCount": 1
}
```

### 7.2 用户指导统计查询接口

#### 7.2.1 根据微信ID和日期查询用户指导统计记录
- **URL**: `GET /api/user-guidance/query`
- **描述**: 根据微信ID和日期查询用户指导统计记录
- **查询参数**:
  - `wechatId` (string, required): 用户微信ID
  - `date` (string, required): 日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /api/user-guidance/query?wechatId=user123&date=2024-01-15
```

**响应示例**:
```json
{
  "createTime": "2024-01-15",
  "wechatId": "user123",
  "guidanceCount": 1,
  "personalizedGuidanceCount": 1
}
```

---

## 8. 常用查询示例

### 订单指标查询
```
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

### 饮食指导分析
```
# 分析对话中的饮食指导情况
curl -X POST "http://localhost:8080/api/user-guidance/analyze" \
  -H "Content-Type: application/json" \
  -d '{
    "wechatId": "user123",
    "date": "2024-01-15T10:30:00",
    "messages": [
      {
        "sender": "客服",
        "message": "建议您控制热量摄入，多吃蔬菜水果",
        "type": "Text",
        "chatTime": "2024-01-15T10:30:00"
      }
    ]
  }'

# 查询用户指导统计记录
curl -X GET "http://localhost:8080