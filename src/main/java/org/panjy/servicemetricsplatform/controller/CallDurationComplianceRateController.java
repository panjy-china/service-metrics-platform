package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.CallStatistics;
import org.panjy.servicemetricsplatform.service.CallDurationComplianceRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电话时长达标率控制器
 * 提供电话时长达标率统计的查询接口
 */
@RestController
@RequestMapping("/api/call-duration-compliance-rate")
public class CallDurationComplianceRateController {
    
    @Autowired
    private CallDurationComplianceRateService callDurationComplianceRateService;
    
    /**
     * 获取所有电话时长达标率统计
     * 
     * @return 电话时长达标率统计列表
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCallDurationComplianceRates() {
        System.out.println("收到获取所有电话时长达标率统计的请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CallStatistics> statistics = callDurationComplianceRateService.calculateCallDurationComplianceRate();
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", statistics);
            
            System.out.println("成功获取电话时长达标率统计，共" + statistics.size() + "条记录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取电话时长达标率统计时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取总电话时长达标率统计
     * 
     * @return 总电话时长达标率统计
     */
    @GetMapping("/total")
    public ResponseEntity<Map<String, Object>> getTotalCallDurationComplianceRate() {
        System.out.println("收到获取总电话时长达标率统计的请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            CallStatistics statistics = callDurationComplianceRateService.calculateTotalCallDurationComplianceRate();
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", statistics);
            
            System.out.println("成功获取总电话时长达标率统计");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取总电话时长达标率统计时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}