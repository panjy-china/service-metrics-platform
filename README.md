# Service Metrics Platform

服务指标平台是一个基于Spring Boot和MyBatis的Java应用程序，用于处理和分析服务指标数据。

## 功能特性

- 客户数据分析
- 微信消息处理
- 订单指标统计
- 地址信息处理
- 大语言模型集成

## 新增功能

### Tbl_Tj_OutCall功能
- 查询符合条件的客户ID列表（去重）
- 根据用户ID查询符合条件的第一条通话记录
  - DNISANS = 1
  - 通话时长大于60秒
  - 按照接听时间升序排列，取第一条

### 三餐打卡与体重反馈分析功能
- 分析用户在微信对话中的三餐打卡情况
- 根据时间段判断用户是否在早餐(6-11点)、午餐(11-13点)、晚餐(17-20点)进行了打卡
- 通过图片消息或饮食反馈文字识别打卡行为
- 智能识别体重反馈信息，特别是在早餐时段发送两次图片的情况

### 三餐打卡与体重反馈分析API
- 提供RESTful API接口分析并存储用户三餐打卡信息
- 支持按日期查询对话记录并进行批量分析
- 自动将分析结果存储到数据库

## 技术栈

- Java 21
- Spring Boot 3.1.2
- MyBatis 3.0.2
- ClickHouse JDBC驱动
- Maven

## 配置

应用程序配置在 `src/main/resources/application.yml` 文件中。

## API端点

- `/api/tbl-tj-out-call/distinct-client-ids` - 获取所有符合条件的客户ID列表
- `/api/tbl-tj-out-call/first-record/{userId}` - 根据用户ID获取第一条符合条件的记录
- `/api/meal-checkin/analyze-and-store/{date}` - 分析并存储三餐打卡信息

## 测试

使用JUnit 5进行单元测试，测试类位于 `src/test/java` 目录下。

## 部署

可以使用Docker进行部署，相关配置在 `docker-compose.yml` 文件中。