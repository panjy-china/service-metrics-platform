package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.CallStatistics;
import org.panjy.servicemetricsplatform.service.CallStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通话统计控制器
 * 提供通话统计数据和个性化指导率的查询接口
 */
@RestController
@RequestMapping("/api/call-statistics")
public class CallStatisticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(CallStatisticsController.class);
    
    @Autowired
    private CallStatisticsService callStatisticsService;
    
    /**
     * 获取所有通话统计数据
     * 
     * @return 通话统计数据列表
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCallStatistics() {
        logger.info("收到获取所有通话统计数据的请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CallStatistics> statistics = callStatisticsService.getAllCallStatistics();
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", statistics);
            
            logger.info("成功获取通话统计数据，共{}条记录", statistics.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("获取通话统计数据时发生异常", e);
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 根据微信ID获取通话统计数据
     * 
     * @param wechatId 微信ID
     * @return 通话统计数据
     */
    @GetMapping("/{wechatId}")
    public ResponseEntity<Map<String, Object>> getCallStatisticsByWechatId(@PathVariable String wechatId) {
        logger.info("收到根据微信ID获取通话统计数据的请求，微信ID: {}", wechatId);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 检查参数
            if (wechatId == null || wechatId.isEmpty()) {
                logger.warn("请求参数错误，微信ID为空");
                response.put("success", false);
                response.put("error", "参数错误");
                response.put("message", "微信ID不能为空");
                return ResponseEntity.badRequest().body(response);
            }
            
            CallStatistics statistics = callStatisticsService.getCallStatisticsByWechatId(wechatId);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", statistics);
            
            logger.info("成功获取微信ID为{}的通话统计数据", wechatId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("根据微信ID获取通话统计数据时发生异常，微信ID: " + wechatId, e);
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}