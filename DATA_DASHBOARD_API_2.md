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

### 2.1 舌苔照片提交率接口

#### 2.1.1 获取舌苔照片提交率
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

### 2.2 体型照片提交率接口

#### 2.2.1 获取体型照片提交率
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

## 3. 不同时间段三餐打卡完成率 API (Meal Check-in Completion Rate by Time Period)

**Base Path**: `/api/meal-checkin-completion-rate`

### 3.1 前三天三餐打卡完成率接口

#### 3.1.1 获取前三天三餐打卡完成率
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

### 3.2 4-6天三餐打卡完成率接口

#### 3.2.1 获取4-6天三餐打卡完成率
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

### 3.3 7-10天三餐打卡完成率接口

#### 3.3.1 获取7-10天三餐打卡完成率
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

## 4. 通话时长统计 API (Call Duration Statistics)

**Base Path**: `/api/call-duration-statistics`

### 4.1 通话时长分布统计接口

#### 4.1.1 获取通话时长分布统计
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