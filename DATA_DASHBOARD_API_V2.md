# 数据看板总接口 API 文档 (Data Dashboard Summary API)

## 概述

数据看板总接口 API 提供了一个整合所有服务指标的统一接口，通过一次调用即可获取所有关键业务指标数据。本文档详细描述了该接口的字段说明和数据来源。

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

## 数据看板总接口 (Data Dashboard Summary API)

**Base Path**: `/api/data-dashboard`

### 获取数据看板所有指标数据

#### 接口信息
- **URL**: `GET /api/data-dashboard/metrics`
- **描述**: 获取数据看板所有指标数据，整合了所有关键业务指标
- **参数**: 无

#### 请求示例
```
GET /api/data-dashboard/metrics
```

#### 响应示例
```json
{
  "data": {
    "dietaryGuidanceReachRate": 56.36,
    "sevenToTenDaysCompletionRate": 0.3065,
    "sixCallComplianceRate": 78.71362940275651,
    "firstCallAverageCompletionTime": 2.592921,
    "bodyTypePhotoSubmissionRate": 0.31528662420382164,
    "tonguePhotoSubmissionRate": 0.46390658174097665,
    "fourCallComplianceRate": 85.45176110260337,
    "traditionalChineseMedicineGuidanceCompletionRate": 56.36,
    "weightFeedbackCompletionRate": "21.79%",
    "userBasicInfoSubmissionRate": "77.92%",
    "callDurationStatistics": [
      {
        "durationRange": "10-15分钟",
        "recordCount": 9077
      },
      {
        "durationRange": "15-20分钟",
        "recordCount": 5677
      },
      {
        "durationRange": "20分钟以上",
        "recordCount": 11453
      },
      {
        "durationRange": "5-10分钟",
        "recordCount": 17912
      },
      {
        "durationRange": "<5分钟",
        "recordCount": 42798
      }
    ],
    "firstCallAverageDuration": {
      "unit": "seconds",
      "averageCallDuration": 839.6556836902801
    },
    "firstCallQualifiedRate": 0.75
    "allUsersMealCheckinRate": "84.30%",
    "fourToSixDaysCompletionRate": 0.3118,
    "firstThreeDaysCompletionRate": 0.2793
  },
  "success": true,
  "message": "查询成功",
  "timestamp": 1760516284846
}
```

#### 字段详细说明

##### 基础信息类指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `userBasicInfoSubmissionRate` | String (百分比) | 用户基础资料提交率，计算公式：(舌苔照片数 + 体型照片数) / 总记录数 × 100% | [UserFirstFeedbackService.calculateBasicInfoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L133-L146) |
| `tonguePhotoSubmissionRate` | Double | 舌苔照片提交比例，计算公式：提交舌苔照片的用户数 / 总用户数 | [UserFirstFeedbackService.calculateTonguePhotoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L43-L55) |
| `bodyTypePhotoSubmissionRate` | Double | 体型照片提交比例，计算公式：提交体型照片的用户数 / 总用户数 | [UserFirstFeedbackService.calculateBodyTypePhotoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L57-L69) |

##### 首电相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `firstCallAverageDuration` | Object | 首电平均通话时长 | [FirstCallSummaryService.calculateAverageCallDuration()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/FirstCallSummaryService.java#L69-L79) |
| `firstCallAverageDuration.averageCallDuration` | Double | 平均通话时长（秒） | [FirstCallSummaryService.calculateAverageCallDuration()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/FirstCallSummaryService.java#L69-L79) |
| `firstCallAverageDuration.unit` | String | 时间单位 | 固定值 "seconds" |
| `firstCallAverageCompletionTime` | BigDecimal | 首电完成平均用时（天），计算公式：符合条件的用时和 / 符合条件的总个数 | [OrderCallTimeDiffService.calculateAverageCallCompletionTimeInDays()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderCallTimeDiffService.java#L36-L63) |
| `firstCallQualifiedRate` | Double | 首电时长达标电话比例，计算公式：时长达标电话总数 / 电话总数的比例，时长达标标准为通话时长超过十分钟(600秒) | [FirstCallSummaryService.calculateQualifiedRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/FirstCallSummaryService.java#L44-L55) |

##### 用户三餐打卡相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `allUsersMealCheckinRate` | String (百分比) | 所有用户三餐打卡率，计算公式：总实际打卡餐数 ÷ (总服务天数 × 3) × 100% | [UserMealCheckinService.calculateAllUsersMealCheckinRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserMealCheckinService.java#L43-L55) |
| `weightFeedbackCompletionRate` | String (百分比) | 体重反馈完成率，计算公式：有体重反馈的记录数 / 总记录数 × 100% | [UserMealCheckinService.calculateWeightFeedbackCompletionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserMealCheckinService.java#L57-L67) |
| `firstThreeDaysCompletionRate` | Double | 前3天三餐打卡完成率 | [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) |
| `fourToSixDaysCompletionRate` | Double | 4～6天三餐打卡完成率 | [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) |
| `sevenToTenDaysCompletionRate` | Double | 7～10天三餐打卡完成率 | [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) |

##### 饮食指导相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `dietaryGuidanceReachRate` | BigDecimal | 饮食指导触达率，计算公式：(个性化指导总次数 / 总指导次数) × 100% | [UserGuidanceStatService.calculateTotalGuidanceReachRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserGuidanceStatService.java#L70-L87) |
| `traditionalChineseMedicineGuidanceCompletionRate` | BigDecimal | 个性化中医指导完成率，计算公式：(个性化指导总次数 / 总指导次数) × 100% | [TraditionalChineseMedicineGuidanceService.calculateTraditionalChineseMedicineGuidanceCompletionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/TraditionalChineseMedicineGuidanceService.java#L26-L46) |

##### 通话相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `fourCallComplianceRate` | Double | 四次通话达标率，计算公式：通话次数>=4的记录数 / 总记录数 × 100% | [CallCountComplianceRateService.calculateFourCallComplianceRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L25-L42) |
| `sixCallComplianceRate` | Double | 六次通话达标率，计算公式：通话次数>=6的记录数 / 总记录数 × 100% | [CallCountComplianceRateService.calculateSixCallComplianceRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L44-L61) |
| `callDurationStatistics` | Array | 通话时长分布统计 | [CallDurationStatisticsService.getCallDurationStatistics()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallDurationStatisticsService.java#L24-L26) |
| `callDurationStatistics[].durationRange` | String | 通话时长区间 | 数据库查询结果 |
| `callDurationStatistics[].recordCount` | Integer | 该时长区间的记录数量 | 数据库查询结果 |

#### 说明
- 该接口整合了所有数据看板的关键业务指标数据
- 包含首电统计、用户基础资料提交统计、用户三餐打卡率统计、饮食指导触达率统计、通话次数统计、首电完成平均用时统计、通话时长统计等所有指标
- 通过一次调用即可获取所有数据看板指标，减少前端请求次数
- 所有百分比数据均保留适当的小数位数，便于展示和分析