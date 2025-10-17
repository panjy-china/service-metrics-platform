# 数据看板 API 文档 (Data Dashboard API)

## 概述

数据看板 API 提供了用于展示服务指标的专用接口，包括首电相关统计数据。本文档描述了数据看板提供的REST API接口。

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

## 1. 首电统计 API (First Call Statistics)

**Base Path**: `/api/first-call-summary`

### 1.1 首电平均通话时长接口

#### 1.1.1 获取所有首通电话的平均通话时长
- **URL**: `GET /api/first-call-summary/average-duration`
- **描述**: 获取所有首通电话的平均通话时长（单位：秒）
- **参数**: 无

**请求示例**:
```
GET /api/first-call-summary/average-duration
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "averageCallDuration": 325.5,
        "unit": "seconds",
        "formattedDuration": "5分25.5秒",
        "description": "所有首通电话的平均通话时长"
    },
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [FirstCallSummaryService.calculateAverageCallDuration()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/FirstCallSummaryService.java#L69-L79) 方法计算平均通话时长
- 只统计通话时长不为空的记录
- 返回结果单位为秒，同时提供格式化的时间显示

### 1.2 时长达标电话比例接口

#### 1.2.1 获取时长达标电话比例
- **URL**: `GET /api/first-call-summary/qualified-rate`
- **描述**: 获取时长达标电话比例
- **参数**: 无

**请求示例**:
```
GET /api/first-call-summary/qualified-rate
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": 0.856,
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [FirstCallSummaryService.calculateQualifiedRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/FirstCallSummaryService.java#L57-L67) 方法计算时长达标电话比例
- 返回结果为小数格式，表示时长达标电话占总电话数的比例

---

## 2. 用户基础资料提交统计 API (User Basic Information Submission Statistics)

**Base Path**: `/api/user-first-feedback`

### 2.1 基础资料提交率接口

#### 2.1.1 获取用户基础资料提交率
- **URL**: `GET /api/user-first-feedback/basic-info-rate`
- **描述**: 获取用户基础资料提交率，计算公式为：(舌苔照片数 + 体型照片数) / 总记录数 * 100%
- **参数**: 无

**请求示例**:
```
GET /api/user-first-feedback/basic-info-rate
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": "85.60%",
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [UserFirstFeedbackService.calculateBasicInfoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L133-L146) 方法计算基础资料提交率
- 返回结果为百分比格式字符串，保留两位小数

### 2.2 基础资料提交统计详情接口

#### 2.2.1 获取用户基础资料提交统计详情
- **URL**: `GET /api/user-first-feedback/basic-info-stats`
- **描述**: 获取用户基础资料提交统计详情，包括舌苔照片和体型照片的总数量以及总记录数
- **参数**: 无

**请求示例**:
```
GET /api/user-first-feedback/basic-info-stats
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": [1712, 2000],
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [UserFirstFeedbackService.getBasicInfoSubmissionStats()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L148-L165) 方法获取统计详情
- 返回结果为数组格式，第一个元素为舌苔照片数+体型照片数的总和，第二个元素为总记录数

### 2.3 舌苔照片提交率接口

#### 2.3.1 获取舌苔照片提交率
- **URL**: `GET /api/user-first-feedback/tongue-photo-rate`
- **描述**: 获取用户舌苔照片提交率，计算公式为：提交舌苔照片的用户数 / 总用户数
- **参数**: 无

**请求示例**:
```
GET /api/user-first-feedback/tongue-photo-rate
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": 0.785,
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [UserFirstFeedbackService.calculateTonguePhotoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L43-L55) 方法计算舌苔照片提交率
- 返回结果为小数格式，表示舌苔照片提交用户占总用户数的比例

### 2.4 体型照片提交率接口

#### 2.4.1 获取体型照片提交率
- **URL**: `GET /api/user-first-feedback/body-type-photo-rate`
- **描述**: 获取用户体型照片提交率，计算公式为：提交体型照片的用户数 / 总用户数
- **参数**: 无

**请求示例**:
```
GET /api/user-first-feedback/body-type-photo-rate
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": 0.692,
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [UserFirstFeedbackService.calculateBodyTypePhotoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L57-L69) 方法计算体型照片提交率
- 返回结果为小数格式，表示体型照片提交用户占总用户数的比例

---

## 3. 用户三餐打卡率统计 API (User Meal Check-in Rate Statistics)

**Base Path**: `/api/meal-checkin-rate`

### 3.1 所有用户三餐打卡率接口

#### 3.1.1 查询所有用户的三餐打卡率
- **URL**: `GET /api/meal-checkin-rate/all`
- **描述**: 计算所有用户的饮食三餐打卡率，计算公式为：打卡率 = 总实际打卡餐数 ÷ (总服务天数 × 3) × 100%
- **参数**: 无

**请求示例**:
```
GET /api/meal-checkin-rate/all
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": "78.50%",
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用相关Service方法计算所有用户的三餐打卡率
- 返回结果为百分比格式字符串，保留两位小数

### 3.2 体重反馈完成率接口

#### 3.2.1 获取体重反馈完成率
- **URL**: `GET /api/meal-checkin-rate/weight-feedback-rate`
- **描述**: 获取用户体重反馈完成率，计算公式为：有体重反馈的记录数 / 总记录数 × 100%
- **参数**: 无

**请求示例**:
```
GET /api/meal-checkin-rate/weight-feedback-rate
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": "65.20%",
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [UserMealCheckinService.calculateWeightFeedbackCompletionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserMealCheckinService.java#L57-L67) 方法计算体重反馈完成率
- 返回结果为百分比格式字符串，保留两位小数

---

## 4. 不同时间段三餐打卡完成率 API (Meal Check-in Completion Rate by Time Period)

**Base Path**: `/api/meal-checkin-completion-rate`

### 4.1 前三天三餐打卡完成率接口

#### 4.1.1 获取前三天三餐打卡完成率
- **URL**: `GET /api/meal-checkin-completion-rate/all`
- **描述**: 获取用户在前三天的三餐打卡完成率
- **参数**: 无

**请求示例**:
```
GET /api/meal-checkin-completion-rate/all
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": [
        {
            "rangeName": "前3天",
            "completionRate": 0.8564
        }
    ],
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) 方法获取前三天三餐打卡完成率
- 返回结果为小数格式，表示前三天三餐打卡完成率

### 4.2 4-6天三餐打卡完成率接口

#### 4.2.1 获取4-6天三餐打卡完成率
- **URL**: `GET /api/meal-checkin-completion-rate/all`
- **描述**: 获取用户在4-6天的三餐打卡完成率
- **参数**: 无

**请求示例**:
```
GET /api/meal-checkin-completion-rate/all
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": [
        {
            "rangeName": "4～6天",
            "completionRate": 0.7245
        }
    ],
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) 方法获取4-6天三餐打卡完成率
- 返回结果为小数格式，表示4-6天三餐打卡完成率

### 4.3 7-10天三餐打卡完成率接口

#### 4.3.1 获取7-10天三餐打卡完成率
- **URL**: `GET /api/meal-checkin-completion-rate/all`
- **描述**: 获取用户在7-10天的三餐打卡完成率
- **参数**: 无

**请求示例**:
```
GET /api/meal-checkin-completion-rate/all
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": [
        {
            "rangeName": "7～10天",
            "completionRate": 0.6523
        }
    ],
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) 方法获取7-10天三餐打卡完成率
- 返回结果为小数格式，表示7-10天三餐打卡完成率

---

## 5. 饮食指导触达率统计 API (Dietary Guidance Reach Rate Statistics)

**Base Path**: `/api/dietary-guidance`

### 5.1 饮食指导触达率接口

#### 5.1.1 获取饮食指导触达率
- **URL**: `GET /api/dietary-guidance/reach-rate`
- **描述**: 获取饮食指导触达率，计算公式为：(个性化指导总次数 / 总指导次数) × 100%
- **参数**: 无

**请求示例**:
```
GET /api/dietary-guidance/reach-rate
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": "72.50",
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [UserGuidanceStatService.calculateTotalGuidanceReachRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserGuidanceStatService.java#L70-L87) 方法计算饮食指导触达率
- 返回结果为BigDecimal格式，保留两位小数

---

## 6. 通话次数统计 API (Call Times Statistics)

**Base Path**: `/api/call-statistics`

### 6.1 四次以上和六次以上电话完成情况接口

#### 6.1.1 获取四次以上和六次以上电话完成情况统计
- **URL**: `GET /api/call-statistics/times-over-4-and-6`
- **描述**: 获取用户通话次数超过4次和超过6次的统计情况
- **参数**: 无

**请求示例**:
```
GET /api/call-statistics/times-over-4-and-6
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": [
        {
            "wechatId": "user123",
            "totalCalls": 8,
            "callsOver4": 1,
            "callsOver6": 1,
            "complianceRate": 1.00
        }
    ],
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口统计用户通话次数超过4次和超过6次的情况
- [callsOver4](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/entity/CallStatistics.java#L25-L25) 表示通话次数超过4次的用户数
- [callsOver6](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/entity/CallStatistics.java#L26-L26) 表示通话次数超过6次的用户数
- [complianceRate](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/entity/CallStatistics.java#L35-L35) 表示达标率，计算公式为：通话次数超过6次的用户数 / 通话次数超过4次的用户数

### 6.2 电话时长达标率统计接口

#### 6.2.1 获取所有电话时长达标率统计
- **URL**: `GET /api/call-statistics/compliance-rate/all`
- **描述**: 获取电话时长达标率统计列表
- **参数**: 无

**请求示例**:
```
GET /api/call-statistics/compliance-rate/all
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": [
        {
            "wechatId": "user123",
            "totalCalls": 150,
            "longCalls": 45,
            "personalizedGuidanceRate": 0.30
        }
    ],
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 CallStatisticsService.calculateCallDurationComplianceRate() 方法计算电话时长达标率
- 达标率计算公式为：calls_over_300s / calls_over_60s
- [totalCalls](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/entity/CallStatistics.java#L24-L24) 表示通话时长超过60秒的通话次数
- [longCalls](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/entity/CallStatistics.java#L25-L25) 表示通话时长超过300秒的通话次数
- [personalizedGuidanceRate](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/entity/CallStatistics.java#L35-L35) 表示时长达标率

---

## 7. 通话次数达标率统计 API (Call Count Compliance Rate Statistics)

**Base Path**: `/api/call-count-compliance-rate`

### 7.1 四次通话达标率接口

#### 7.1.1 获取四次通话达标率
- **URL**: `GET /api/call-count-compliance-rate/four-call-rate`
- **描述**: 获取四次通话达标率，计算公式为：通话次数>=4的记录数 / 总记录数 × 100%
- **参数**: 无

**请求示例**:
```
GET /api/call-count-compliance-rate/four-call-rate
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": 75.50,
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [CallCountComplianceRateService.calculateFourCallComplianceRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L25-L42) 方法计算四次通话达标率
- 返回结果为百分比数值，保留两位小数

### 7.2 六次通话达标率接口

#### 7.2.1 获取六次通话达标率
- **URL**: `GET /api/call-count-compliance-rate/six-call-rate`
- **描述**: 获取六次通话达标率，计算公式为：通话次数>=6的记录数 / 总记录数 × 100%
- **参数**: 无

**请求示例**:
```
GET /api/call-count-compliance-rate/six-call-rate
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": 62.25,
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [CallCountComplianceRateService.calculateSixCallComplianceRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L44-L61) 方法计算六次通话达标率
- 返回结果为百分比数值，保留两位小数

### 7.3 通话次数统计详情接口

#### 7.3.1 获取通话次数统计详情
- **URL**: `GET /api/call-count-compliance-rate/statistics`
- **描述**: 获取通话次数统计详情，包括总记录数、四次通话达标数、六次通话达标数及对应达标率
- **参数**: 无

**请求示例**:
```
GET /api/call-count-compliance-rate/statistics
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "totalCount": 1000,
        "fourCallCompliantCount": 755,
        "sixCallCompliantCount": 623,
        "fourCallComplianceRate": 75.50,
        "sixCallComplianceRate": 62.25
    },
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [CallCountComplianceRateService.getCallCountStatistics()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L63-L87) 方法获取通话次数统计详情
- [totalCount](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L68-L68) 表示总记录数
- [fourCallCompliantCount](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L72-L72) 表示通话次数>=4的记录数
- [sixCallCompliantCount](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L76-L76) 表示通话次数>=6的记录数
- [fourCallComplianceRate](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L78-L78) 表示四次通话达标率
- [sixCallComplianceRate](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L79-L79) 表示六次通话达标率

---

## 8. 首电完成平均用时统计 API (First Call Completion Average Time Statistics)

**Base Path**: `/api/order-call-time-diff`

### 8.1 首电完成平均用时接口

#### 8.1.1 获取首电完成平均用时
- **URL**: `GET /api/order-call-time-diff/average-time`
- **描述**: 获取首电完成平均用时，计算公式为：符合条件的用时和 / 符合条件的总个数，单位为天
- **过滤条件**:
  - 去除时间为负数的记录
  - 去除秒数大于432000的记录（5天）
- **参数**: 无

**请求示例**:
```
GET /api/order-call-time-diff/average-time
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": 1.256432,
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [OrderCallTimeDiffService.calculateAverageCallCompletionTimeInDays()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderCallTimeDiffService.java#L36-L63) 方法计算首电完成平均用时
- 返回结果为BigDecimal格式，保留6位小数，单位为天

### 8.2 首电完成时间差统计接口

#### 8.2.1 获取首电完成时间差统计信息
- **URL**: `GET /api/order-call-time-diff/statistics`
- **描述**: 获取首电完成时间差的统计信息，包括总记录数、符合条件记录数和平均用时
- **过滤条件**:
  - 去除时间为负数的记录
  - 去除秒数大于432000的记录（5天）
- **参数**: 无

**请求示例**:
```
GET /api/order-call-time-diff/statistics
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "totalCount": 1000,
        "filteredCount": 856,
        "averageTimeInDays": 1.256432
    },
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [OrderCallTimeDiffService.getStatistics()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderCallTimeDiffService.java#L82-L111) 方法获取统计信息
- [totalCount](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderCallTimeDiffService.java#L89-L89) 表示总记录数
- [filteredCount](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderCallTimeDiffService.java#L95-L95) 表示符合条件记录数
- [averageTimeInDays](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderCallTimeDiffService.java#L96-L96) 表示平均用时（天）

---

## 9. 通话时长统计 API (Call Duration Statistics)

**Base Path**: `/api/call-duration-statistics`

### 9.1 通话时长分布统计接口

#### 9.1.1 获取通话时长分布统计
- **URL**: `GET /api/call-duration-statistics/all`
- **描述**: 获取不同通话时长区间的记录数量统计
- **参数**: 无

**请求示例**:
```
GET /api/call-duration-statistics/all
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": [
        {
            "durationRange": "<5分钟",
            "recordCount": 1250
        },
        {
            "durationRange": "5-10分钟",
            "recordCount": 865
        },
        {
            "durationRange": "10-15分钟",
            "recordCount": 423
        },
        {
            "durationRange": "15-20分钟",
            "recordCount": 156
        },
        {
            "durationRange": "20分钟以上",
            "recordCount": 89
        }
    ],
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口调用 [CallDurationStatisticsService.getCallDurationStatistics()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallDurationStatisticsService.java#L24-L26) 方法获取通话时长分布统计
- 统计区间包括：<5分钟、5-10分钟、10-15分钟、15-20分钟、20分钟以上
- 返回结果为各时长区间的记录数量

---

## 10. 数据看板总接口 (Data Dashboard Summary API)

**Base Path**: `/api/data-dashboard`

### 10.1 数据看板所有指标接口

#### 10.1.1 获取数据看板所有指标数据
- **URL**: `GET /api/data-dashboard/metrics`
- **描述**: 获取数据看板所有指标数据，整合了所有数据看板的指标
- **参数**: 无

**请求示例**:
```
GET /api/data-dashboard/metrics
```

**响应示例**:
```json
{
    "success": true,
    "message": "查询成功",
    "data": {
        "firstCallAverageDuration": {
            "averageCallDuration": 325.5,
            "unit": "seconds",
            "formattedDuration": "5分25.5秒",
            "description": "所有首通电话的平均通话时长"
        },
        "userBasicInfoSubmissionRate": "85.60%",
        "userBasicInfoSubmissionStats": {
            "tonguePhotoAndBodyTypePhotoCount": 1712,
            "totalRecordCount": 2000
        },
        "allUsersMealCheckinRate": "78.50%",
        "weightFeedbackCompletionRate": "65.20%",
        "dietaryGuidanceReachRate": 72.50,
        "fourCallComplianceRate": 75.50,
        "sixCallComplianceRate": 62.25,
        "callCountStatistics": {
            "totalCount": 1000,
            "fourCallCompliantCount": 755,
            "sixCallCompliantCount": 623,
            "fourCallComplianceRateDetail": 75.50,
            "sixCallComplianceRateDetail": 62.25
        },
        "firstCallAverageCompletionTime": 1.256432,
        "firstCallTimeDiffStatistics": {
            "totalCount": 1000,
            "filteredCount": 856,
            "averageTimeInDays": 1.256432
        },
        "callDurationStatistics": [
            {
                "durationRange": "<5分钟",
                "recordCount": 1250
            },
            {
                "durationRange": "5-10分钟",
                "recordCount": 865
            },
            {
                "durationRange": "10-15分钟",
                "recordCount": 423
            },
            {
                "durationRange": "15-20分钟",
                "recordCount": 156
            },
            {
                "durationRange": "20分钟以上",
                "recordCount": 89
            }
        ]
    },
    "timestamp": 1705123456789
}
```

**说明**:
- 该接口整合了所有数据看板的指标数据
- 包含首电统计、用户基础资料提交统计、用户三餐打卡率统计、饮食指导触达率统计、通话次数统计、首电完成平均用时统计、通话时长统计等所有指标
- 通过一次调用即可获取所有数据看板指标，减少前端请求次数