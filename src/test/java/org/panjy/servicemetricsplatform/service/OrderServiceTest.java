package org.panjy.servicemetricsplatform.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

/**
 * OrderService测试类
 * 测试当月人均成交订单数和人均销售额计算功能
 * 计算公式：人均指标 = 总指标 / Constants.ACCOUNT_NUMBER常量
 */
@SpringBootTest
public class OrderServiceTest {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceTest.class);
    
    @Autowired
    private OrderService orderService;
    
    @Test
    public void testCalculateCurrentMonthAvgOrdersPerCustomer() {
        logger.info("测试当月人均成交订单数计算");
        
        BigDecimal avgOrders = orderService.calculateCurrentMonthAvgOrdersPerCustomer();
        
        logger.info("当月人均成交订单数: {}", avgOrders);
        
        // 验证结果不为null且不为负数
        assert avgOrders != null;
        assert avgOrders.compareTo(BigDecimal.ZERO) >= 0;
        
        logger.info("当月人均成交订单数测试通过");
    }
    
    @Test
    public void testCalculateCurrentMonthAvgSalesPerCustomer() {
        logger.info("测试当月人均成交销售额计算");
        
        BigDecimal avgSales = orderService.calculateCurrentMonthAvgSalesPerCustomer();
        
        logger.info("当月人均成交销售额: {}", avgSales);
        
        // 验证结果不为null且不为负数
        assert avgSales != null;
        assert avgSales.compareTo(BigDecimal.ZERO) >= 0;
        
        logger.info("当月人均成交销售额测试通过");
    }
    
    @Test
    public void testCalculateCurrentMonthStats() {
        logger.info("测试当月综合统计数据");
        
        Map<String, Object> stats = orderService.calculateCurrentMonthStats();
        
        logger.info("当月综合统计数据: {}", stats);
        
        // 验证关键字段存在
        assert stats.containsKey("period");
        assert stats.containsKey("totalSales");
        assert stats.containsKey("totalOrders");
        assert stats.containsKey("totalCustomers");
        assert stats.containsKey("avgOrdersPerCustomer");
        assert stats.containsKey("avgSalesPerCustomer");
        
        // 验证人均指标为正数或零
        BigDecimal avgOrders = (BigDecimal) stats.get("avgOrdersPerCustomer");
        BigDecimal avgSales = (BigDecimal) stats.get("avgSalesPerCustomer");
        
        assert avgOrders != null && avgOrders.compareTo(BigDecimal.ZERO) >= 0;
        assert avgSales != null && avgSales.compareTo(BigDecimal.ZERO) >= 0;
        
        logger.info("当月综合统计数据测试通过");
    }
    
    @Test
    public void testMethodsReturnConsistentResults() {
        logger.info("测试方法结果一致性");
        
        // 单独调用方法
        BigDecimal avgOrders1 = orderService.calculateCurrentMonthAvgOrdersPerCustomer();
        BigDecimal avgSales1 = orderService.calculateCurrentMonthAvgSalesPerCustomer();
        
        // 通过综合方法调用
        Map<String, Object> stats = orderService.calculateCurrentMonthStats();
        BigDecimal avgOrders2 = (BigDecimal) stats.get("avgOrdersPerCustomer");
        BigDecimal avgSales2 = (BigDecimal) stats.get("avgSalesPerCustomer");
        
        // 验证结果一致性
        assert avgOrders1.equals(avgOrders2) : "人均订单数结果不一致";
        assert avgSales1.equals(avgSales2) : "人均销售额结果不一致";
        
        logger.info("方法结果一致性测试通过");
        logger.info("人均订单数: {}, 人均销售额: {}", avgOrders1, avgSales1);
    }
}