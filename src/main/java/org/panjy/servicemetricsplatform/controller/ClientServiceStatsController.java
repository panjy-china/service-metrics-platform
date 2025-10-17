package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.ClientServiceStats;
import org.panjy.servicemetricsplatform.service.ClientServiceStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 客户服务统计控制器
 */
@RestController
@RequestMapping("/api/client-service-stats")
public class ClientServiceStatsController {
    
    @Autowired
    private ClientServiceStatsService clientServiceStatsService;
    
    
    /**
     * 获取推单成交率
     * @return 推单成交率
     */
    @GetMapping("/conversion-rate")
    public Map<String, Object> getPushOrderConversionRate() {
        double conversionRate = clientServiceStatsService.calculatePushOrderConversionRate();
        
        // 返回详细信息
        return Map.of(
            "conversionRate", conversionRate,
            "percentage", String.format("%.2f", conversionRate * 100)
        );
    }
}