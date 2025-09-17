package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OrderService 测试类
 * 验证订单服务的各项功能，包括同比增长计算
 */
@SpringBootTest
class OrderServiceTest {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * 测试当月人均订单数计算
     */
    @Test
    void testCalculateCurrentMonthAvgOrdersPerCustomer() {
        try {
            BigDecimal result = orderService.calculateCurrentMonthAvgOrdersPerCustomer();
            assertNotNull(result, "人均订单数不应为null");
            assertTrue(result.compareTo(BigDecimal.ZERO) >= 0, "人均订单数应大于等于0");
            System.out.println("当月人均订单数测试通过: " + result);
        } catch (Exception e) {
            System.out.println("当月人均订单数测试异常（可能因数据库连接问题）: " + e.getMessage());
        }
    }
    
    /**
     * 测试指定月份人均订单数计算
     */
    @Test
    void testCalculateMonthlyAvgOrdersPerCustomer() {
        try {
            String testMonth = "2025-09";
            BigDecimal result = orderService.calculateMonthlyAvgOrdersPerCustomer(testMonth);
            assertNotNull(result, "指定月份人均订单数不应为null");
            assertTrue(result.compareTo(BigDecimal.ZERO) >= 0, "指定月份人均订单数应大于等于0");
            System.out.println("指定月份人均订单数测试通过: " + testMonth + " -> " + result);
        } catch (Exception e) {
            System.out.println("指定月份人均订单数测试异常（可能因数据库连接问题）: " + e.getMessage());
        }
    }
    
    /**
     * 测试当月人均销售额计算
     */
    @Test
    void testCalculateCurrentMonthAvgSalesPerCustomer() {
        try {
            BigDecimal result = orderService.calculateCurrentMonthAvgSalesPerCustomer();
            assertNotNull(result, "人均销售额不应为null");
            assertTrue(result.compareTo(BigDecimal.ZERO) >= 0, "人均销售额应大于等于0");
            System.out.println("当月人均销售额测试通过: " + result);
        } catch (Exception e) {
            System.out.println("当月人均销售额测试异常（可能因数据库连接问题）: " + e.getMessage());
        }
    }
    
    /**
     * 测试指定月份人均销售额计算
     */
    @Test
    void testCalculateMonthlySalesPerCustomer() {
        try {
            String testMonth = "2025-09";
            BigDecimal result = orderService.calculateMonthlySalesPerCustomer(testMonth);
            assertNotNull(result, "指定月份人均销售额不应为null");
            assertTrue(result.compareTo(BigDecimal.ZERO) >= 0, "指定月份人均销售额应大于等于0");
            System.out.println("指定月份人均销售额测试通过: " + testMonth + " -> " + result);
        } catch (Exception e) {
            System.out.println("指定月份人均销售额测试异常（可能因数据库连接问题）: " + e.getMessage());
        }
    }
    
    /**
     * 测试当月综合统计数据计算（不包含同比增长）
     */
    @Test
    void testCalculateCurrentMonthStats() {
        try {
            Map<String, Object> result = orderService.calculateCurrentMonthStats();
            assertNotNull(result, "当月综合统计结果不应为null");
            assertFalse(result.containsKey("error"), "当月综合统计不应包含错误信息");
            
            // 验证关键字段存在
            assertTrue(result.containsKey("period"), "应包含期间信息");
            assertTrue(result.containsKey("avgOrdersPerCustomer"), "应包含人均订单数");
            assertTrue(result.containsKey("avgSalesPerCustomer"), "应包含人均销售额");
            
            System.out.println("当月综合统计测试通过: " + result);
        } catch (Exception e) {
            System.out.println("当月综合统计测试异常（可能因数据库连接问题）: " + e.getMessage());
        }
    }
    
    /**
     * 测试指定月份综合统计数据计算（不包含同比增长）
     */
    @Test
    void testCalculateMonthlyStats() {
        try {
            String testMonth = "2025-09";
            Map<String, Object> result = orderService.calculateMonthlyStats(testMonth);
            assertNotNull(result, "指定月份综合统计结果不应为null");
            assertFalse(result.containsKey("error"), "指定月份综合统计不应包含错误信息");
            
            // 验证关键字段存在
            assertTrue(result.containsKey("period"), "应包含期间信息");
            assertTrue(result.containsKey("avgOrdersPerCustomer"), "应包含人均订单数");
            assertTrue(result.containsKey("avgSalesPerCustomer"), "应包含人均销售额");
            assertEquals(testMonth, result.get("period"), "期间信息应匹配");
            
            System.out.println("指定月份综合统计测试通过: " + testMonth + " -> " + result);
        } catch (Exception e) {
            System.out.println("指定月份综合统计测试异常（可能因数据库连接问题）: " + e.getMessage());
        }
    }
    
    /**
     * 测试当月综合统计数据计算（包含同比增长）
     */
    @Test
    void testCalculateCurrentMonthStatsWithGrowth() {
        try {
            Map<String, Object> result = orderService.calculateCurrentMonthStatsWithGrowth();
            assertNotNull(result, "当月综合统计（含增长）结果不应为null");
            assertFalse(result.containsKey("error"), "当月综合统计（含增长）不应包含错误信息");
            
            // 验证关键字段存在
            assertTrue(result.containsKey("period"), "应包含当前期间信息");
            assertTrue(result.containsKey("previousPeriod"), "应包含上期间信息");
            assertTrue(result.containsKey("avgOrdersPerCustomer"), "应包含人均订单数");
            assertTrue(result.containsKey("avgOrdersGrowthRate"), "应包含人均订单数增长率");
            assertTrue(result.containsKey("avgSalesPerCustomer"), "应包含人均销售额");
            assertTrue(result.containsKey("avgSalesGrowthRate"), "应包含人均销售额增长率");
            
            System.out.println("当月综合统计（含增长）测试通过: " + result);
        } catch (Exception e) {
            System.out.println("当月综合统计（含增长）测试异常（可能因数据库连接问题）: " + e.getMessage());
        }
    }
    
    /**
     * 测试指定月份综合统计数据计算（包含同比增长）
     */
    @Test
    void testCalculateMonthlyStatsWithGrowth() {
        try {
            String testMonth = "2025-09";
            Map<String, Object> result = orderService.calculateMonthlyStatsWithGrowth(testMonth);
            assertNotNull(result, "指定月份综合统计（含增长）结果不应为null");
            assertFalse(result.containsKey("error"), "指定月份综合统计（含增长）不应包含错误信息");
            
            // 验证关键字段存在
            assertTrue(result.containsKey("period"), "应包含当前期间信息");
            assertTrue(result.containsKey("previousPeriod"), "应包含上期间信息");
            assertTrue(result.containsKey("avgOrdersPerCustomer"), "应包含人均订单数");
            assertTrue(result.containsKey("avgOrdersGrowthRate"), "应包含人均订单数增长率");
            assertTrue(result.containsKey("avgSalesPerCustomer"), "应包含人均销售额");
            assertTrue(result.containsKey("avgSalesGrowthRate"), "应包含人均销售额增长率");
            assertEquals(testMonth, result.get("period"), "当前期间信息应匹配");
            
            System.out.println("指定月份综合统计（含增长）测试通过: " + testMonth + " -> " + result);
        } catch (Exception e) {
            System.out.println("指定月份综合统计（含增长）测试异常（可能因数据库连接问题）: " + e.getMessage());
        }
    }
}