package org.panjy.servicemetricsplatform.util;

import org.panjy.servicemetricsplatform.entity.Conversation;
import org.panjy.servicemetricsplatform.entity.Message;
import org.panjy.servicemetricsplatform.entity.UserMealCheckin;
import org.panjy.servicemetricsplatform.service.LLMAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 三餐打卡与体重反馈分析演示类
 * 用于测试和演示analyzeMealCheckinAndWeightFeedback方法的功能
 */
@Component
public class MealCheckinAnalysisDemo {

    @Autowired
    private LLMAnalysisService llmAnalysisService;

    /**
     * 演示三餐打卡与体重反馈分析功能
     */
    public void demonstrateMealCheckinAnalysis() {
        System.out.println("=== 三餐打卡与体重反馈分析演示 ===");

        // 创建测试对话记录
        Conversation conversation = createTestConversation();

        // 调用分析方法
        UserMealCheckin result = llmAnalysisService.analyzeMealCheckinAndWeightFeedback(conversation);

        // 输出结果
        System.out.println("分析结果:");
        System.out.println("用户微信ID: " + result.getWechatId());
        System.out.println("打卡日期: " + result.getCheckinDate());
        System.out.println("早餐打卡: " + (result.getBreakfastChecked() == 1 ? "是" : "否"));
        System.out.println("午餐打卡: " + (result.getLunchChecked() == 1 ? "是" : "否"));
        System.out.println("晚餐打卡: " + (result.getDinnerChecked() == 1 ? "是" : "否"));
        System.out.println("体重反馈: " + (result.getHasWeightFeedback() == 1 ? "是" : "否"));
    }

    /**
     * 创建测试对话记录
     * @return 测试用的Conversation对象
     */
    private Conversation createTestConversation() {
        Conversation conversation = new Conversation();
        conversation.setWechatId("test_user_001");
        conversation.setDate(LocalDateTime.now());

        List<Message> messages = new ArrayList<>();

        // 添加早餐时间段的消息 (8:30)
        messages.add(new Message("test_user_001", "这里发送了一张早餐照片", "Picture", 
                                LocalDateTime.of(2023, 10, 15, 8, 30)));

        // 添加午餐时间段的消息 (12:15)
        messages.add(new Message("test_user_001", "今天午餐吃了宫保鸡丁和米饭", "Text", 
                                LocalDateTime.of(2023, 10, 15, 12, 15)));

        // 添加晚餐时间段的消息 (18:45)
        messages.add(new Message("test_user_001", "这里发送了一张晚餐照片", "Picture", 
                                LocalDateTime.of(2023, 10, 15, 18, 45)));

        // 添加体重反馈消息 (20:30)
        messages.add(new Message("test_user_001", "今天称了下体重，比上周轻了2斤", "Text", 
                                LocalDateTime.of(2023, 10, 15, 20, 30)));

        conversation.setMessages(messages);
        return conversation;
    }

    /**
     * 创建另一个测试对话记录（包含两次早餐图片）
     * @return 测试用的Conversation对象
     */
    private Conversation createTestConversationWithTwoBreakfastImages() {
        Conversation conversation = new Conversation();
        conversation.setWechatId("test_user_002");
        conversation.setDate(LocalDateTime.now());

        List<Message> messages = new ArrayList<>();

        // 添加早餐时间段的两张图片 (8:30, 8:35)
        messages.add(new Message("test_user_002", "这里发送了一张早餐照片", "Picture", 
                                LocalDateTime.of(2023, 10, 15, 8, 30)));
        messages.add(new Message("test_user_002", "这里又发送了一张早餐照片", "Picture", 
                                LocalDateTime.of(2023, 10, 15, 8, 35)));

        // 添加体重反馈消息 (8:40)
        messages.add(new Message("test_user_002", "今天称了下体重，比上周轻了1公斤", "Text", 
                                LocalDateTime.of(2023, 10, 15, 8, 40)));

        conversation.setMessages(messages);
        return conversation;
    }

    /**
     * 演示包含两次早餐图片的分析
     */
    public void demonstrateTwoBreakfastImagesAnalysis() {
        System.out.println("\n=== 包含两次早餐图片的分析演示 ===");

        // 创建测试对话记录
        Conversation conversation = createTestConversationWithTwoBreakfastImages();

        // 调用分析方法
        UserMealCheckin result = llmAnalysisService.analyzeMealCheckinAndWeightFeedback(conversation);

        // 输出结果
        System.out.println("分析结果:");
        System.out.println("用户微信ID: " + result.getWechatId());
        System.out.println("打卡日期: " + result.getCheckinDate());
        System.out.println("早餐打卡: " + (result.getBreakfastChecked() == 1 ? "是" : "否"));
        System.out.println("午餐打卡: " + (result.getLunchChecked() == 1 ? "是" : "否"));
        System.out.println("晚餐打卡: " + (result.getDinnerChecked() == 1 ? "是" : "否"));
        System.out.println("体重反馈: " + (result.getHasWeightFeedback() == 1 ? "是" : "否"));
    }
}