# 服务指标平台API文档

## 综合指标接口

### 接口地址
`GET /api/comprehensive/metrics/{date}`

### 请求参数
| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| date | String | 是 | 查询日期，格式为 `yyyy-MM-dd` |

### 响应格式
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    // 综合指标数据
  },
  "timestamp": 1760516284846
}
```

### 响应字段说明

#### 客户分布相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `genderDistribution` | Array | 客户性别分布 | [ClientService.getGenderDistribution()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/ClientService.java#L683-L696) |
| `genderDistribution[].gender` | String | 性别 | 数据库查询结果 |
| `genderDistribution[].count` | Integer | 该性别人数 | 数据库查询结果 |
| `ageDistribution` | Array | 客户年龄分布 | [ClientService.getAgeDistribution()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/ClientService.java#L704-L720) |
| `ageDistribution[].ageRange` | String | 年龄区间 | 数据库查询结果 |
| `ageDistribution[].count` | Integer | 该年龄区间人数 | 数据库查询结果 |
| `weightDistribution` | Array | 客户体重分布 | [ClientService.getWeightDistribution()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/ClientService.java#L728-L744) |
| `weightDistribution[].weightRange` | String | 体重区间 | 数据库查询结果 |
| `weightDistribution[].count` | Integer | 该体重区间人数 | 数据库查询结果 |
| `regionDistribution` | Array | 客户地区分布 | [WechatMessageAnalyzeAddressMapper.getUserLatestAddresses()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/mapper/WechatMessageAnalyzeAddressMapper.java#L119-L121) |
| `regionDistribution[].province` | String | 省份 | 地址解析结果 |
| `regionDistribution[].count` | Integer | 该省份人数 | 地址解析结果 |

#### 新增用户相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `dailyNewUsers` | Object | 日新增用户及环比 | [StrategicLayerService.calculateDailyNewUsersWithGrowth()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/StrategicLayerService.java#L845-L870) |
| `dailyNewUsers.currentValue` | Integer | 当日新增用户数 | 数据库查询结果 |
| `dailyNewUsers.previousValue` | Integer | 前一日新增用户数 | 数据库查询结果 |
| `dailyNewUsers.growthRate` | String | 环比增长率 | 计算结果 |
| `weeklyNewUsers` | Object | 周新增用户及环比 | [StrategicLayerService.calculateWeeklyNewUsersWithGrowth()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/StrategicLayerService.java#L878-L903) |
| `weeklyNewUsers.currentValue` | Integer | 当周新增用户数 | 数据库查询结果 |
| `weeklyNewUsers.previousValue` | Integer | 上周新增用户数 | 数据库查询结果 |
| `weeklyNewUsers.growthRate` | String | 环比增长率 | 计算结果 |
| `monthlyNewUsers` | Object | 月新增用户及环比 | [StrategicLayerService.calculateMonthlyNewUsersWithGrowth()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/StrategicLayerService.java#L911-L936) |
| `monthlyNewUsers.currentValue` | Integer | 当月新增用户数 | 数据库查询结果 |
| `monthlyNewUsers.previousValue` | Integer | 上月新增用户数 | 数据库查询结果 |
| `monthlyNewUsers.growthRate` | String | 环比增长率 | 计算结果 |

#### 用户留存率相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `threeDayRetentionRate` | Object | 三日留存率及同比增长 | [StrategicLayerService.calculateRetentionRateWithGrowth()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/StrategicLayerService.java#L769-L791) |
| `threeDayRetentionRate.currentValue` | String | 当前三日留存率 | 数据库查询结果 |
| `threeDayRetentionRate.previousValue` | String | 上期三日留存率 | 数据库查询结果 |
| `threeDayRetentionRate.growthRate` | String | 同比增长率 | 计算结果 |
| `sevenDayRetentionRate` | Object | 七日留存率及同比增长 | [StrategicLayerService.calculateRetentionRateWithGrowth()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/StrategicLayerService.java#L769-L791) |
| `sevenDayRetentionRate.currentValue` | String | 当前七日留存率 | 数据库查询结果 |
| `sevenDayRetentionRate.previousValue` | String | 上期七日留存率 | 数据库查询结果 |
| `sevenDayRetentionRate.growthRate` | String | 同比增长率 | 计算结果 |
| `tenDayRetentionRate` | Object | 十日留存率及同比增长 | [StrategicLayerService.calculateRetentionRateWithGrowth()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/StrategicLayerService.java#L769-L791) |
| `tenDayRetentionRate.currentValue` | String | 当前十日留存率 | 数据库查询结果 |
| `tenDayRetentionRate.previousValue` | String | 上期十日留存率 | 数据库查询结果 |
| `tenDayRetentionRate.growthRate` | String | 同比增长率 | 计算结果 |

#### 用户流失率相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `sevenDayChurnRate` | Object | 七日流失率及同比增长 | [StrategicLayerService.calculateChurnRateWithGrowth()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/StrategicLayerService.java#L813-L837) |
| `sevenDayChurnRate.currentValue` | String | 当前七日流失率 | 数据库查询结果 |
| `sevenDayChurnRate.previousValue` | String | 上期七日流失率 | 数据库查询结果 |
| `sevenDayChurnRate.growthRate` | String | 同比增长率 | 计算结果 |

#### 用户转化率相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `tenDayConversionRate` | String | 十日转化率 | [OrderService.calculateTenDayConversionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L821-L865) |
| `fifteenDayConversionRate` | String | 十五日转化率 | [OrderService.calculateFifteenDayConversionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L921-L965) |

#### 订单相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `currentMonthAvgOrdersPerCustomer` | String | 当月人均成交订单数 | [OrderService.calculateCurrentMonthAvgOrdersPerCustomer()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L533-L539) |
| `currentMonthAvgSalesPerCustomer` | String | 当月人均成交销售额 | [OrderService.calculateCurrentMonthAvgSalesPerCustomer()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L569-L575) |
| `avgOrdersGrowthRate` | String | 人均订单数环比增长率 | [OrderService.calculateGrowthRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L503-L525) |
| `avgSalesGrowthRate` | String | 人均销售额环比增长率 | [OrderService.calculateGrowthRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L503-L525) |

#### 服务时间相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `averageServiceTime` | Object | 平均服务时间及同比增长 | [StrategicLayerService.calculateAverageServiceTimeWithGrowth()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/StrategicLayerService.java#L944-L966) |
| `averageServiceTime.currentValue` | String | 当前平均服务时间 | 数据库查询结果 |
| `averageServiceTime.previousValue` | String | 上期平均服务时间 | 数据库查询结果 |
| `averageServiceTime.growthRate` | String | 同比增长率 | 计算结果 |
| `averageDealTime` | Object | 平均成交时间 | [OrderService.calculateAverageServiceTime()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L647-L709) |
| `averageDealTime.currentValue` | String | 平均成交时间 | 数据库查询结果 |

#### 订单统计数据相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `orderStatistics` | Object | 订单统计数据 | [OrderStatisticsService.getOrderStatisticsByDate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderStatisticsService.java#L29-L52) |
| `orderStatistics.daily` | Object | 日订单统计数据 | 数据库查询结果 |
| `orderStatistics.daily.count` | Integer | 日订单数 | 数据库查询结果 |
| `orderStatistics.daily.growthRate` | String | 日订单数环比增长率 | 计算结果 |
| `orderStatistics.weekly` | Object | 周订单统计数据 | 数据库查询结果 |
| `orderStatistics.weekly.count` | Integer | 周订单数 | 数据库查询结果 |
| `orderStatistics.weekly.growthRate` | String | 周订单数环比增长率 | 计算结果 |
| `orderStatistics.monthly` | Object | 月订单统计数据 | 数据库查询结果 |
| `orderStatistics.monthly.count` | Integer | 月订单数 | 数据库查询结果 |
| `orderStatistics.monthly.growthRate` | String | 月订单数环比增长率 | 计算结果 |

#### 下单用户数统计相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `orderingUserStats` | Object | 下单用户数统计 | [OrderService.getOrderingUserStatsWithGrowthByDate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L977-L1013) |
| `orderingUserStats.daily` | Object | 日下单用户数统计 | 数据库查询结果 |
| `orderingUserStats.daily.count` | Long | 日下单用户数 | 数据库查询结果 |
| `orderingUserStats.daily.previousCount` | Long | 前一日下单用户数 | 数据库查询结果 |
| `orderingUserStats.daily.growthRate` | String | 日下单用户数环比增长率 | 计算结果 |
| `orderingUserStats.weekly` | Object | 周下单用户数统计 | 数据库查询结果 |
| `orderingUserStats.weekly.count` | Long | 周下单用户数 | 数据库查询结果 |
| `orderingUserStats.weekly.previousCount` | Long | 前一周下单用户数 | 数据库查询结果 |
| `orderingUserStats.weekly.growthRate` | String | 周下单用户数环比增长率 | 计算结果 |
| `orderingUserStats.monthly` | Object | 月下单用户数统计 | 数据库查询结果 |
| `orderingUserStats.monthly.count` | Long | 月下单用户数 | 数据库查询结果 |
| `orderingUserStats.monthly.previousCount` | Long | 前一月下单用户数 | 数据库查询结果 |
| `orderingUserStats.monthly.growthRate` | String | 月下单用户数环比增长率 | 计算结果 |

#### 销售总额统计相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `salesAmountStats` | Object | 销售总额统计 | [OrderService.getSalesAmountStatsWithGrowthByDate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L435-L477) |
| `salesAmountStats.daily` | Object | 日销售总额统计 | 数据库查询结果 |
| `salesAmountStats.daily.amount` | String | 日销售总额 | 数据库查询结果 |
| `salesAmountStats.daily.growthRate` | String | 日销售总额环比增长率 | 计算结果 |
| `salesAmountStats.weekly` | Object | 周销售总额统计 | 数据库查询结果 |
| `salesAmountStats.weekly.amount` | String | 周销售总额 | 数据库查询结果 |
| `salesAmountStats.weekly.growthRate` | String | 周销售总额环比增长率 | 计算结果 |
| `salesAmountStats.monthly` | Object | 月销售总额统计 | 数据库查询结果 |
| `salesAmountStats.monthly.amount` | String | 月销售总额 | 数据库查询结果 |
| `salesAmountStats.monthly.growthRate` | String | 月销售总额环比增长率 | 计算结果 |

#### 平均客单价相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `averageOrderValueStats` | Object | 平均客单价统计 | [OrderService.calculateWeeklyAverageOrderValue()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderService.java#L75-L115) |
| `averageOrderValueStats.weeklyAverageOrderValue` | String | 周平均客单价 | 数据库查询结果 |
| `averageOrderValueStats.monthlyAverageOrderValue` | String | 月平均客单价 | 数据库查询结果 |
| `averageOrderValueStats.weeklyGrowthRate` | String | 周平均客单价环比增长率 | 计算结果 |
| `averageOrderValueStats.monthlyGrowthRate` | String | 月平均客单价环比增长率 | 计算结果 |

#### 销售排行榜相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `salesRankingData` | Object | 销售排行榜数据 | [SalesRankingService.getSalesRankingsByDate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/SalesRankingService.java#L33-L50) |
| `salesRankingData.date` | String | 统计日期 | 参数传入 |
| `salesRankingData.weeklyRankings` | Array | 周销售排行榜 | [SalesRankingMapper.getWeeklySalesRanking()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/mapper/SalesRankingMapper.java#L51-L53) |
| `salesRankingData.weeklyRankings[].salesCode` | String | 销售员代码 | 数据库查询结果 |
| `salesRankingData.weeklyRankings[].salesName` | String | 销售员姓名 | 数据库查询结果 |
| `salesRankingData.weeklyRankings[].totalSales` | String | 总销售额 | 数据库查询结果 |
| `salesRankingData.monthlyRankings` | Array | 月销售排行榜 | [SalesRankingMapper.getMonthlySalesRanking()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/mapper/SalesRankingMapper.java#L55-L57) |
| `salesRankingData.monthlyRankings[].salesCode` | String | 销售员代码 | 数据库查询结果 |
| `salesRankingData.monthlyRankings[].salesName` | String | 销售员姓名 | 数据库查询结果 |
| `salesRankingData.monthlyRankings[].totalSales` | String | 总销售额 | 数据库查询结果 |

**响应示例**:
```json
{
  "success": true,
  "message": "查询成功",
  "data": {
    "genderDistribution": [
      {
        "gender": "男",
        "count": 1234
      },
      {
        "gender": "女",
        "count": 5678
      }
    ],
    "ageDistribution": [
      {
        "ageRange": "18-25",
        "count": 123
      },
      {
        "ageRange": "26-35",
        "count": 456
      }
    ],
    "weightDistribution": [
      {
        "weightRange": "40-50",
        "count": 78
      },
      {
        "weightRange": "50-60",
        "count": 156
      }
    ],
    "regionDistribution": [
      {
        "province": "北京",
        "count": 890
      },
      {
        "province": "上海",
        "count": 780
      }
    ],
    "dailyNewUsers": {
      "currentValue": 123,
      "previousValue": 100,
      "growthRate": "23.00"
    },
    "weeklyNewUsers": {
      "currentValue": 456,
      "previousValue": 400,
      "growthRate": "14.00"
    },
    "monthlyNewUsers": {
      "currentValue": 1234,
      "previousValue": 1100,
      "growthRate": "12.18"
    },
    "threeDayRetentionRate": {
      "currentValue": "75.00",
      "previousValue": "70.00",
      "growthRate": "7.14"
    },
    "sevenDayRetentionRate": {
      "currentValue": "65.00",
      "previousValue": "60.00",
      "growthRate": "8.33"
    },
    "tenDayRetentionRate": {
      "currentValue": "55.00",
      "previousValue": "50.00",
      "growthRate": "10.00"
    },
    "sevenDayChurnRate": {
      "currentValue": "35.00",
      "previousValue": "40.00",
      "growthRate": "-12.50"
    },
    "tenDayConversionRate": "0.4567",
    "fifteenDayConversionRate": "0.5678",
    "currentMonthAvgOrdersPerCustomer": "2.34",
    "currentMonthAvgSalesPerCustomer": "1234.56",
    "avgOrdersGrowthRate": "5.67",
    "avgSalesGrowthRate": "8.90",
    "averageServiceTime": {
      "currentValue": "15.67",
      "previousValue": "14.23",
      "growthRate": "10.12"
    },
    "averageDealTime": {
      "currentValue": "7.89"
    },
    "orderStatistics": {
      "daily": {
        "count": 123,
        "growthRate": "5.67"
      },
      "weekly": {
        "count": 456,
        "growthRate": "8.90"
      },
      "monthly": {
        "count": 1234,
        "growthRate": "12.34"
      }
    },
    "orderingUserStats": {
      "daily": {
        "count": 98,
        "previousCount": 85,
        "growthRate": "15.29"
      },
      "weekly": {
        "count": 345,
        "previousCount": 300,
        "growthRate": "15.00"
      },
      "monthly": {
        "count": 987,
        "previousCount": 876,
        "growthRate": "12.67"
      }
    },
    "salesAmountStats": {
      "daily": {
        "amount": "12345.67",
        "growthRate": "8.90"
      },
      "weekly": {
        "amount": "89012.34",
        "growthRate": "12.34"
      },
      "monthly": {
        "amount": "345678.90",
        "growthRate": "15.67"
      }
    },
    "averageOrderValueStats": {
      "weeklyAverageOrderValue": "258.74",
      "monthlyAverageOrderValue": "350.23",
      "weeklyGrowthRate": "5.67",
      "monthlyGrowthRate": "8.90"
    },
    "salesRankingData": {
      "date": "2025-10-17",
      "weeklyRankings": [
        {
          "salesCode": "S001",
          "salesName": "张三",
          "totalSales": "12345.67"
        },
        {
          "salesCode": "S002",
          "salesName": "李四",
          "totalSales": "9876.54"
        }
      ],
      "monthlyRankings": [
        {
          "salesCode": "S001",
          "salesName": "张三",
          "totalSales": "56789.01"
        },
        {
          "salesCode": "S003",
          "salesName": "王五",
          "totalSales": "45678.90"
        }
      ]
    }
  },
  "timestamp": 1760516284846
}
```

#### 字段详细说明

##### 基础信息类指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `userBasicInfoSubmissionRate` | String | 用户基础资料提交率，计算公式：(舌苔照片数 + 体型照片数) / 总记录数 × 100% | [UserFirstFeedbackService.calculateBasicInfoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L133-L146) |
| `tonguePhotoSubmissionRate` | String | 舌苔照片提交比例，计算公式：提交舌苔照片的用户数 / 总用户数 × 100 | [UserFirstFeedbackService.calculateTonguePhotoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L43-L55) |
| `bodyTypePhotoSubmissionRate` | String | 体型照片提交比例，计算公式：提交体型照片的用户数 / 总用户数 × 100 | [UserFirstFeedbackService.calculateBodyTypePhotoSubmissionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserFirstFeedbackService.java#L57-L69) |

##### 首电相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `firstCallAverageDuration` | String | 首电平均通话时长（分钟） | [FirstCallSummaryService.calculateAverageCallDuration()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/FirstCallSummaryService.java#L69-L79) |
| `firstCallQualifiedRate` | String | 首电时长达标电话比例，计算公式：时长达标电话总数 / 电话总数的比例，时长达标标准为通话时长超过十分钟(600秒) | [FirstCallSummaryService.calculateQualifiedRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/FirstCallSummaryService.java#L44-L55) |

##### 用户三餐打卡相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `allUsersMealCheckinRate` | String | 所有用户三餐打卡率，计算公式：总实际打卡餐数 ÷ (总服务天数 × 3) × 100% | [UserMealCheckinService.calculateAllUsersMealCheckinRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserMealCheckinService.java#L43-L55) |
| `weightFeedbackCompletionRate` | String | 体重反馈完成率，计算公式：有体重反馈的记录数 / 总记录数 × 100% | [UserMealCheckinService.calculateWeightFeedbackCompletionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserMealCheckinService.java#L57-L67) |
| `firstThreeDaysCompletionRate` | String | 前3天三餐打卡完成率 | [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) |
| `fourToSixDaysCompletionRate` | String | 4～6天三餐打卡完成率 | [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) |
| `sevenToTenDaysCompletionRate` | String | 7～10天三餐打卡完成率 | [MealCheckinCompletionRateService.getMealCheckinCompletionRates()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/MealCheckinCompletionRateService.java#L23-L25) |

##### 饮食指导相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `dietaryGuidanceReachRate` | String | 饮食指导触达率，计算公式：(个性化指导总次数 / 总指导次数) × 100% | [UserGuidanceStatService.calculateTotalGuidanceReachRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/UserGuidanceStatService.java#L70-L87) |
| `traditionalChineseMedicineGuidanceCompletionRate` | String | 个性化中医指导完成率，计算公式：(个性化指导总次数 / 总指导次数) × 100% | [TraditionalChineseMedicineGuidanceService.calculateTraditionalChineseMedicineGuidanceCompletionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/TraditionalChineseMedicineGuidanceService.java#L26-L46) |

##### 通话相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `fourCallComplianceRate` | String | 四次通话达标率，计算公式：通话次数>=4的记录数 / 总记录数 × 100% | [CallCountComplianceRateService.calculateFourCallComplianceRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L25-L42) |
| `sixCallComplianceRate` | String | 六次通话达标率，计算公式：通话次数>=6的记录数 / 总记录数 × 100% | [CallCountComplianceRateService.calculateSixCallComplianceRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallCountComplianceRateService.java#L44-L61) |
| `callDurationStatistics` | Array | 通话时长分布统计 | [CallDurationStatisticsService.getCallDurationStatistics()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/CallDurationStatisticsService.java#L24-L26) |
| `callDurationStatistics[].durationRange` | String | 通话时长区间 | 数据库查询结果 |
| `callDurationStatistics[].recordCount` | Integer | 该时长区间的记录数量 | 数据库查询结果 |

##### 首电完成时间相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `firstCallAverageCompletionTime` | String | 首电完成平均用时（天），计算公式：符合条件的用时和 / 符合条件的总个数 | [OrderCallTimeDiffService.calculateAverageCallCompletionTimeInDays()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/OrderCallTimeDiffService.java#L36-L63) |

##### 推单相关指标

| 字段名 | 类型 | 描述 | 数据来源 |
|--------|------|------|----------|
| `pushOrderConversionRate` | String | 推单成交率，计算公式：满足条件的客户数 / 客户总数 | [ClientServiceStatsService.calculatePushOrderConversionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/ClientServiceStatsService.java#L42-L50) |
| `orderRetentionRate` | String | 推单后留存率 | [ServerTimeService.calculateOrderRetentionRate()](file:///e:/Panjy-codeing/java/service-metrics-platform/src/main/java/org/panjy/servicemetricsplatform/service/ServerTimeService.java#L137-L149) |

---