package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.CallCountComplianceRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 通话次数达标率控制器
 * 提供四次通话达标率和六次通话达标率的查询接口
 */
@RestController
@RequestMapping("/api/call-count-compliance-rate")
public class CallCountComplianceRateController {
    
    @Autowired
    private CallCountComplianceRateService callCountComplianceRateService;
    
    /**
     * 获取四次通话达标率
     * 
     * @return 四次通话达标率
     */
    @GetMapping("/four-call-rate")
    public ResponseEntity<Map<String, Object>> getFourCallComplianceRate() {
        System.out.println("收到获取四次通话达标率的请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            double rate = callCountComplianceRateService.calculateFourCallComplianceRate();
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", rate);
            
            System.out.println("成功获取四次通话达标率: " + rate + "%");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取四次通话达标率时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取六次通话达标率
     * 
     * @return 六次通话达标率
     */
    @GetMapping("/six-call-rate")
    public ResponseEntity<Map<String, Object>> getSixCallComplianceRate() {
        System.out.println("收到获取六次通话达标率的请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            double rate = callCountComplianceRateService.calculateSixCallComplianceRate();
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", rate);
            
            System.out.println("成功获取六次通话达标率: " + rate + "%");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取六次通话达标率时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * 获取通话次数统计信息
     * 
     * @return 通话次数统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getCallCountStatistics() {
        System.out.println("收到获取通话次数统计信息的请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            long[] statistics = callCountComplianceRateService.getCallCountStatistics();
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalCount", statistics[0]);
            data.put("fourCallCompliantCount", statistics[1]);
            data.put("sixCallCompliantCount", statistics[2]);
            data.put("fourCallComplianceRate", statistics[0] > 0 ? (double) statistics[1] / statistics[0] * 100 : 0.0);
            data.put("sixCallComplianceRate", statistics[0] > 0 ? (double) statistics[2] / statistics[0] * 100 : 0.0);
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", data);
            
            System.out.println("成功获取通话次数统计信息");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取通话次数统计信息时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}