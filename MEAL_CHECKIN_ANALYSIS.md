# 三餐打卡与体重反馈分析功能说明

## 功能概述

本功能用于分析用户在微信对话中的三餐打卡情况和体重反馈信息。通过分析用户在特定时间段发送的消息，判断用户是否完成了三餐打卡，并识别是否有体重反馈信息。

## 核心功能

### 1. 三餐打卡识别

根据用户发送消息的时间段判断三餐打卡情况：

- **早餐打卡**：6:00-11:00时间段内发送图片或饮食反馈文字
- **午餐打卡**：11:00-13:00时间段内发送图片或饮食反馈文字
- **晚餐打卡**：17:00-20:00时间段内发送图片或饮食反馈文字

### 2. 体重反馈识别

识别用户是否提供了体重反馈信息：

- 直接识别包含体重关键词的消息（如"体重"、"斤"、"公斤"、"kg"等）
- 对于早餐时间段发送两次图片的情况，结合上下文判断是否包含体重反馈
- 使用大语言模型进行智能分析，提高识别准确率

## 技术实现

### 主要方法

```java
public UserMealCheckin analyzeMealCheckinAndWeightFeedback(Conversation conversation)
```

### 判断逻辑

1. **时间段判断**：
   - 早餐：6点～11点
   - 午餐：11点～13点
   - 晚餐：17点～20点

2. **打卡识别**：
   - 图片消息（类型为"Picture"或"WxPic"）
   - 饮食反馈文字（包含饮食相关关键词）

3. **体重反馈识别**：
   - 直接关键词匹配
   - 早餐两次图片+上下文分析
   - 大语言模型辅助分析

### 饮食关键词

包含但不限于以下关键词：
- 餐类：早餐、午餐、晚餐、早饭、午饭、晚饭
- 饮食行为：吃了、吃的是、今天吃、喝的、喝了
- 食物类型：面条、米饭、粥、包子、馒头、面包、牛奶、豆浆、咖啡、蔬菜、水果、肉类等

### 体重关键词

包含但不限于以下关键词：
- 体重、斤、公斤、kg、KG、减重、增重、轻了、重了、称重

## 使用示例

```java
// 创建对话记录
Conversation conversation = new Conversation();
conversation.setWechatId("user123");
conversation.setDate(LocalDateTime.now());

// 添加消息
List<Message> messages = new ArrayList<>();
messages.add(new Message("user123", "这里发送了一张早餐照片", "Picture", 
                        LocalDateTime.of(2023, 10, 15, 8, 30)));
messages.add(new Message("user123", "今天称了下体重，比上周轻了2斤", "Text", 
                        LocalDateTime.of(2023, 10, 15, 20, 30)));
conversation.setMessages(messages);

// 分析
UserMealCheckin result = llmAnalysisService.analyzeMealCheckinAndWeightFeedback(conversation);

// 结果
System.out.println("早餐打卡: " + (result.getBreakfastChecked() == 1 ? "是" : "否"));
System.out.println("体重反馈: " + (result.getHasWeightFeedback() == 1 ? "是" : "否"));
```

## 数据结构

### UserMealCheckin实体类

| 字段名 | 类型 | 说明 |
|-------|------|------|
| wechatId | String | 用户微信ID |
| checkinDate | LocalDate | 打卡日期 |
| breakfastChecked | Integer | 早餐是否打卡 (0=否, 1=是) |
| lunchChecked | Integer | 午餐是否打卡 (0=否, 1=是) |
| dinnerChecked | Integer | 晚餐是否打卡 (0=否, 1=是) |
| hasWeightFeedback | Integer | 是否有体重反馈 (0=否, 1=是) |

## 注意事项

1. 时间判断基于消息的发送时间(chatTime)
2. 图片消息类型包括"Picture"和"WxPic"
3. 饮食反馈和体重反馈的关键词识别支持模糊匹配
4. 对于复杂的上下文分析，会调用大语言模型进行辅助判断
5. 异常情况下会返回默认的UserMealCheckin对象