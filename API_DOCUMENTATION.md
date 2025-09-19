# Service Metrics Platform API 文档

## 概述

Service Metrics Platform 是一个服务指标分析平台，提供微信相关服务的运营指标分析功能。本文档描述了平台提供的REST API接口。

## 基础信息

- **Base URL**: `http://47.100.81.66:9172`
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

## 1. 策略层指标 API (Strategic Metrics)

**Base Path**: `/api/strategic`

### 1.1 新增用户相关接口

#### 1.1.1 查询日新增用户
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

#### 1.1.2 查询周新增用户
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

#### 1.1.3 查询月新增用户
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

### 1.2 新增用户增长率接口

#### 1.2.1 日新增用户增长率
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

#### 1.2.2 周新增用户增长率
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

#### 1.2.3 月新增用户增长率
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

### 1.3 活跃用户接口

#### 1.3.1 活跃用户数增长率
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

### 1.4 流失率接口

#### 1.4.1 用户流失率
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

### 1.5 服务时间接口

#### 1.5.1 平均服务时间增长率
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

### 1.6 综合概览接口

#### 1.6.1 综合指标概览
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

## 2. 地址分析 API (Address Analysis)

**Base Path**: `/analyze/address`

### 2.1 地址分析接口

#### 2.1.1 分析指定日期包含地址的消息
- **URL**: `GET /analyze/address/{date}`
- **描述**: 分析指定日期包含地址关键词的消息，使用LLM提取地址信息并保存到数据库
- **路径参数**:
  - `date` (string, required): 分析日期，格式: `yyyy-MM-dd`

**请求示例**:
```
GET /analyze/address/2024-01-15
```

**响应示例**:
```json
{
    "success": true,
    "message": "地址分析处理完成",
    "data": [...],
    "totalFound": 10,
    "processedCount": 10,
    "extractedAddresses": 8,
    "savedToDatabase": 8,
    "queryDate": "2024-01-15",
    "timestamp": 1705123456789
}
```

---

## 3. 订单指标 API (Order Metrics)

**Base Path**: `/api/order-metrics`

### 3.1 人均订单数接口

#### 3.1.1 指定月份人均订单数
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

#### 3.1.2 当月人均订单数
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

### 3.2 人均销售额接口

#### 3.2.1 指定月份人均销售额
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

#### 3.2.2 当月人均销售额
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

### 3.3 综合统计接口

#### 3.3.1 指定月份综合统计
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

#### 3.3.2 当月综合统计
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

### 3.4 概览接口

#### 3.4.1 订单指标概览
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

### 3.5 健康检查接口

#### 3.5.1 服务健康检查
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

### 3.6 服务时间接口

#### 3.6.1 获取指定客户的服务时间
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

#### 3.6.2 获取所有客户的平均成交天数
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

### 3.7 客户分析接口

#### 3.7.1 获取指定日期之后首次下单的客户列表
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

#### 3.7.2 计算指定日期之后用户的十日成交转换率
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

#### 3.7.3 计算指定日期之后用户的十五日成交转换率
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

## 注意事项

1. **日期格式**: 所有日期参数必须严格按照 `yyyy-MM-dd` 格式提供
2. **月份格式**: 月份参数必须按照 `YYYY-MM` 格式提供
3. **数值精度**: 所有数值计算结果保留2位小数
4. **增长率计算**: 增长率 = (当前值 - 上期值) / 上期值 × 100%
5. **时区**: 所有时间均基于服务器本地时区
6. **数据范围**: 查询范围依赖于数据库中的实际数据

## 使用示例

### 获取综合业务概览
```bash
# 获取策略指标概览
curl -X GET "http://localhost:8080/api/strategic/overview-growth/2024-01-15"

# 获取订单指标概览
curl -X GET "http://localhost:8080/api/order-metrics/overview"
```

### 监控业务增长
```bash
# 监控日新增用户增长
curl -X GET "http://localhost:8080/api/strategic/new-users/daily-growth/2024-01-15"

# 监控月度订单指标增长
curl -X GET "http://localhost:8080/api/order-metrics/monthly-stats/2024-01"
```

---

*最后更新时间: 2025-09-19*