package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.OrderRetentionRate;
import org.panjy.servicemetricsplatform.service.ServerTimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 推单三日留存率控制器
 * 提供推单三日留存率相关的API接口
 */
@RestController
@RequestMapping("/api/order-retention")
public class OrderRetentionRateController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderRetentionRateController.class);
    
    @Autowired
    private ServerTimeService serverTimeService;
    
    /**
     * 获取推单三日留存率
     * 
     * @return 留存率数据
     */
    @GetMapping("/rate")
    public OrderRetentionRate getOrderRetentionRate() {
        logger.info("收到获取推单三日留存率的请求");
        return serverTimeService.calculateOrderRetentionRate();
    }
}