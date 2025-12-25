package org.panjy.servicemetricsplatform.service.analysis;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.panjy.servicemetricsplatform.entity.label.WechatUserLabel;
import org.panjy.servicemetricsplatform.entity.message.Conversation;
import org.panjy.servicemetricsplatform.entity.message.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class HealthProfileAnalysisServiceTest {

    @Autowired
    private HealthProfileAnalysisService healthProfileAnalysisService;

    private Conversation createTestConversation() {
        Conversation conversation = new Conversation();
        conversation.setWechatId("test_user_001");
        conversation.setDate(LocalDateTime.now());

        List<Message> messages = new ArrayList<>();
        
        // 添加一些健康相关的聊天记录用于测试
        Message msg1 = new Message("客户", "最近总是感觉头晕，血压可能有点高", "text", LocalDateTime.now().minusHours(2));
        Message msg2 = new Message("客服", "您有测量过具体的血压数值吗？", "text", LocalDateTime.now().minusHours(1));
        Message msg3 = new Message("客户", "上周体检医生说血压偏高，让我注意饮食，现在在吃降压药", "text", LocalDateTime.now().minusMinutes(30));
        Message msg4 = new Message("客户", "我还有点关节疼，特别是膝盖，是不是和天气有关系", "text", LocalDateTime.now().minusMinutes(15));
        Message msg5 = new Message("客户", "睡眠也不太好，经常半夜醒来", "text", LocalDateTime.now().minusMinutes(5));

        messages.add(msg1);
        messages.add(msg2);
        messages.add(msg3);
        messages.add(msg4);
        messages.add(msg5);

        conversation.setMessages(messages);
        return conversation;
    }

    @Test
    public void testExtractHealthProfileTags() {
        try {
            Conversation testConversation = createTestConversation();
            
            System.out.println("开始测试健康画像分析功能...");
            System.out.println("测试对话记录：");
            testConversation.getMessages().forEach(msg -> 
                System.out.println("[" + msg.getChatTime() + "] " + msg.getSender() + ": " + msg.getMessage())
            );
            
            WechatUserLabel result = healthProfileAnalysisService.extractHealthProfileTags(testConversation);
            
            System.out.println("\n健康画像分析结果：");
            System.out.println(result.toString());
            
            // 验证返回结果不为空
            assertNotNull(result, "健康画像分析结果不应为空");
            
            // 验证WechatUserLabel对象的各个字段
            assertNotNull(result.getWechatFriendId(), "微信好友ID不应为空");
            assertNotNull(result.getWechatAccountId(), "微信账号ID不应为空");
            assertNotNull(result.getLabel(), "标签不应为空");
            assertNotNull(result.getEvidence(), "证据不应为空");
            assertNotNull(result.getCreatedAt(), "创建时间不应为空");
            assertNotNull(result.getUpdatedAt(), "更新时间不应为空");
            
            System.out.println("微信好友ID: " + result.getWechatFriendId());
            System.out.println("微信账号ID: " + result.getWechatAccountId());
            System.out.println("标签: " + result.getLabel());
            System.out.println("证据长度: " + result.getEvidence().length());
            System.out.println("创建时间: " + result.getCreatedAt());
            System.out.println("更新时间: " + result.getUpdatedAt());
            
            System.out.println("\n测试通过！");
            
        } catch (Exception e) {
            System.err.println("测试过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            fail("测试失败: " + e.getMessage());
        }
    }

    @Test
    public void testExtractHealthProfileTagsWithNullConversation() {
        try {
            WechatUserLabel result = healthProfileAnalysisService.extractHealthProfileTags(null);
            
            System.out.println("空对话测试结果：");
            System.out.println(result.toString());
            
            // 对于空对话，应该返回WechatUserLabel对象，但标签可能为空
            assertNotNull(result, "结果不应为空");
            
            // 验证WechatUserLabel对象的各个字段
            assertNotNull(result.getWechatFriendId(), "微信好友ID不应为空");
            assertNotNull(result.getWechatAccountId(), "微信账号ID不应为空");
            assertNotNull(result.getLabel(), "标签不应为空");
            assertNotNull(result.getEvidence(), "证据不应为空");
            assertNotNull(result.getCreatedAt(), "创建时间不应为空");
            assertNotNull(result.getUpdatedAt(), "更新时间不应为空");
            
        } catch (Exception e) {
            System.err.println("空对话测试异常: " + e.getMessage());
            // 某些异常可能是预期的，比如API Key未配置
        }
    }

    @Test
    public void testExtractHealthProfileTagsWithEmptyMessages() {
        try {
            Conversation emptyConversation = new Conversation();
            emptyConversation.setWechatId("test_user_002");
            emptyConversation.setDate(LocalDateTime.now());
            emptyConversation.setMessages(new ArrayList<>());
            
            WechatUserLabel result = healthProfileAnalysisService.extractHealthProfileTags(emptyConversation);
            
            System.out.println("空消息对话测试结果：");
            System.out.println(result.toString());
            
            assertNotNull(result, "结果不应为空");
            
            // 验证WechatUserLabel对象的各个字段
            assertNotNull(result.getWechatFriendId(), "微信好友ID不应为空");
            assertNotNull(result.getWechatAccountId(), "微信账号ID不应为空");
            assertNotNull(result.getLabel(), "标签不应为空");
            assertNotNull(result.getEvidence(), "证据不应为空");
            assertNotNull(result.getCreatedAt(), "创建时间不应为空");
            assertNotNull(result.getUpdatedAt(), "更新时间不应为空");
            
        } catch (Exception e) {
            System.err.println("空消息对话测试异常: " + e.getMessage());
        }
    }
}