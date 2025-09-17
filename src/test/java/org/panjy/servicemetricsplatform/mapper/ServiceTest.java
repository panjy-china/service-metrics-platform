package org.panjy.servicemetricsplatform.mapper;

import org.junit.jupiter.api.Test;
import org.panjy.servicemetricsplatform.service.StrategicLayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * StrategicLayerService测试类
 * 验证战略层服务功能是否正确
 */
@SpringBootTest
public class ServiceTest {
    
    @Autowired
    private StrategicLayerService strategicLayerService;

    @Test
    public void testFindNewUserByMonth() {
        try {
            // 创建2025年8月1日的Date对象
            LocalDate localDate = LocalDate.of(2025, 8, 1);
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            
            List<String> newUserByMonth = strategicLayerService.findNewUserByMonth(date);
            
            System.out.println("查询到的新用户数量: " + newUserByMonth.size());
            
            // 验证结果不为null
            assert newUserByMonth != null : "查询结果不应为null";
            
            // 打印前几个用户（如果存在）
            if (!newUserByMonth.isEmpty()) {
                System.out.println("前5个新用户: ");
                newUserByMonth.stream().limit(5).forEach(System.out::println);
            } else {
                System.out.println("该月份没有新用户");
            }
            
        } catch (Exception e) {
            System.err.println("测试执行失败: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常以便测试框架捕获
        }
    }
    
    @Test
    public void testApplicationContextLoads() {
        // 简单的应用上下文加载测试
        System.out.println("应用上下文加载成功！");
        
        // 验证服务注入成功
        assert strategicLayerService != null : "StrategicLayerService应该被正确注入";
        
        System.out.println("StrategicLayerService注入成功: " + strategicLayerService.getClass().getSimpleName());
    }
}