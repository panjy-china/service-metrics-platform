package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.panjy.servicemetricsplatform.entity.Message;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LLM分析服务修改后的功能演示测试
 */
@SpringBootTest
public class LLMAnalysisServiceEnhancedTest {
    
    @Autowired
    private LLMAnalysisService llmAnalysisService;
    
    /**
     * 测试简化版本的地址分析和存储
     */
    @Test
    public void testAnalyzeAndStoreAddressesSimple() throws Exception {
        System.out.println("=== 测试简化版本的地址分析和存储 ===");
        
        List<Message> testMessages = createTestMessages();
        
        // 使用简化版本
        int savedCount = llmAnalysisService.analyzeAndStoreAddressesSimple(testMessages, 3);
        
        System.out.println("简化版本执行结果 - 成功存储: " + savedCount + " 条地址记录");
        System.out.println("=== 简化版本测试完成 ===\n");
    }
    
    /**
     * 测试完整版本的地址分析和存储（带详细结果）
     */
    @Test
    public void testAnalyzeAndStoreAddressesWithDetails() throws Exception {
        System.out.println("=== 测试完整版本的地址分析和存储 ===");
        
        List<Message> testMessages = createTestMessages();
        
        // 使用完整版本
        WechatMessageAnalyzeAddressService.ExecuteResult result = 
            llmAnalysisService.analyzeAndStoreAddressesWithDetails(testMessages, 3);
        
        System.out.println("完整版本执行结果:");
        System.out.println("- 执行状态: " + (result.isSuccess() ? "成功" : "失败"));
        System.out.println("- 消息: " + result.getMessage());
        
        if (result.getErrorCode() != null) {
            System.out.println("- 错误代码: " + result.getErrorCode());
        }
        
        if (result.getData() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) result.getData();
            
            System.out.println("- 详细信息:");
            details.forEach((key, value) -> {
                if (!"processingLogs".equals(key) && !"storageResult".equals(key)) {
                    System.out.println("  " + key + ": " + value);
                }
            });
            
            // 显示处理日志
            if (details.containsKey("processingLogs")) {
                @SuppressWarnings("unchecked")
                List<String> logs = (List<String>) details.get("processingLogs");
                System.out.println("- 处理日志:");
                logs.forEach(log -> System.out.println("  " + log));
            }
            
            // 显示存储结果详情
            if (details.containsKey("storageResult")) {
                WechatMessageAnalyzeAddressService.ExecuteResult storageResult = 
                    (WechatMessageAnalyzeAddressService.ExecuteResult) details.get("storageResult");
                System.out.println("- 存储操作详情:");
                System.out.println("  状态: " + (storageResult.isSuccess() ? "成功" : "失败"));
                System.out.println("  消息: " + storageResult.getMessage());
            }
        }
        
        System.out.println("=== 完整版本测试完成 ===\n");
    }
    
    /**
     * 对比测试原始版本和新版本
     */
    @Test
    public void testCompareOriginalAndNewVersions() throws Exception {
        System.out.println("=== 对比测试原始版本和新版本 ===");
        
        List<Message> testMessages = createTestMessages();
        
        // 测试原始版本
        System.out.println("1. 原始版本测试:");
        int originalResult = llmAnalysisService.analyzeAndStoreAddresses(testMessages, 3);
        System.out.println("   原始版本结果: " + originalResult + " 条");
        
        // 测试简化版本
        System.out.println("2. 简化版本测试:");
        int simpleResult = llmAnalysisService.analyzeAndStoreAddressesSimple(testMessages, 3);
        System.out.println("   简化版本结果: " + simpleResult + " 条");
        
        // 测试完整版本
        System.out.println("3. 完整版本测试:");
        WechatMessageAnalyzeAddressService.ExecuteResult detailedResult = 
            llmAnalysisService.analyzeAndStoreAddressesWithDetails(testMessages, 3);
        System.out.println("   完整版本状态: " + (detailedResult.isSuccess() ? "成功" : "失败"));
        
        if (detailedResult.getData() instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> details = (Map<String, Object>) detailedResult.getData();
            System.out.println("   完整版本结果: " + details.get("savedAddresses") + " 条");
        }
        
        System.out.println("=== 对比测试完成 ===");
    }
    
    /**
     * 创建测试消息数据
     */
    private List<Message> createTestMessages() {
        List<Message> messages = new ArrayList<>();
        
        messages.add(new Message("user001", "我在北京市朝阳区三里屯工作", "text", LocalDateTime.now()));
        messages.add(new Message("user002", "请发货到上海市浦东新区陆家嘴金融中心", "text", LocalDateTime.now().minusMinutes(5)));
        messages.add(new Message("user003", "广州市天河区珠江新城天河路123号", "text", LocalDateTime.now().minusMinutes(10)));
        messages.add(new Message("user004", "深圳市南山区科技园南区", "text", LocalDateTime.now().minusMinutes(15)));
        messages.add(new Message("user005", "今天天气不错，适合出门", "text", LocalDateTime.now().minusMinutes(20))); // 不含地址
        messages.add(new Message("user006", "杭州市西湖区文三路508号天苑大厦", "text", LocalDateTime.now().minusMinutes(25)));
        
        return messages;
    }
}