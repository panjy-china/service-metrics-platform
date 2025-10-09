package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.MealCheckinCompletionRate;
import org.panjy.servicemetricsplatform.service.MealCheckinCompletionRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 餐食打卡完成率控制器
 * 提供餐食打卡完成率的查询接口
 */
@RestController
@RequestMapping("/api/meal-checkin-completion-rate")
public class MealCheckinCompletionRateController {
    
    @Autowired
    private MealCheckinCompletionRateService mealCheckinCompletionRateService;
    
    /**
     * 获取不同时间段的餐食打卡完成率
     * 
     * @return 餐食打卡完成率列表
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMealCheckinCompletionRates() {
        System.out.println("收到获取餐食打卡完成率的请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<MealCheckinCompletionRate> rates = mealCheckinCompletionRateService.getMealCheckinCompletionRates();
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", rates);
            
            System.out.println("成功获取餐食打卡完成率，共" + rates.size() + "条记录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取餐食打卡完成率时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}