package org.panjy.servicemetricsplatform.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.panjy.servicemetricsplatform.entity.Message;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 地址分析控制器测试类
 */
@SpringBootTest
public class AnalysisAddressControllerTest {
    
    @Autowired
    private AnalysisAddressController analysisAddressController;
    
    /**
     * 测试地址分析功能
     */
    @Test
    public void testFindMessagesLikeAddress() throws Exception {
        // 准备测试日期（查询最近7天的数据）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date testDate = calendar.getTime();
        
        System.out.println("=== 开始测试地址分析功能 ===");
        System.out.println("查询日期: " + testDate);
        
        try {
            // 调用地址分析方法
            List<Message> results = analysisAddressController.findMessagesLikeAddress(testDate);
            
            System.out.println("分析完成，返回结果数量: " + (results != null ? results.size() : 0));
            
            if (results != null && !results.isEmpty()) {
                System.out.println("地址分析结果示例（前3条）:");
                int count = Math.min(3, results.size());
                for (int i = 0; i < count; i++) {
                    Message msg = results.get(i);
                    System.out.println("- 发送者: " + msg.getSender());
                    System.out.println("  分析结果: " + msg.getMessage());
                    System.out.println("  类型: " + msg.getType());
                    System.out.println("  时间: " + msg.getChatTime());
                    System.out.println("---");
                }
            } else {
                System.out.println("没有找到地址分析结果，可能原因:");
                System.out.println("1. 指定日期范围内没有包含地址关键词的消息");
                System.out.println("2. LLM分析未识别出有效地址信息");
                System.out.println("3. 数据库中没有相关数据");
            }
            
        } catch (Exception e) {
            System.err.println("测试过程中发生错误: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        
        System.out.println("=== 地址分析功能测试完成 ===");
    }
    
    /**
     * 测试不同日期范围的地址分析
     */
    @Test
    public void testFindMessagesLikeAddressWithDifferentDates() throws Exception {
        System.out.println("=== 测试不同日期范围的地址分析 ===");
        
        // 测试最近1天
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date oneDayAgo = calendar.getTime();
        
        List<Message> results1Day = analysisAddressController.findMessagesLikeAddress(oneDayAgo);
        System.out.println("最近1天的地址分析结果数量: " + (results1Day != null ? results1Day.size() : 0));
        
        // 测试最近30天
        calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = calendar.getTime();
        
        List<Message> results30Days = analysisAddressController.findMessagesLikeAddress(thirtyDaysAgo);
        System.out.println("最近30天的地址分析结果数量: " + (results30Days != null ? results30Days.size() : 0));
        
        System.out.println("=== 不同日期范围测试完成 ===");
    }
}