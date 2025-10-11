package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.TraditionalChineseMedicineGuidanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 个性化中医指导完成率控制器
 * 提供个性化中医指导完成率的API接口
 */
@RestController
@RequestMapping("/api/traditional-chinese-medicine-guidance")
@CrossOrigin(origins = "*") // 允许跨域请求
public class TraditionalChineseMedicineGuidanceController {

    @Autowired
    private TraditionalChineseMedicineGuidanceService traditionalChineseMedicineGuidanceService;

    /**
     * 获取总的个性化中医指导完成率
     *
     * @return 个性化中医指导完成率
     */
    @GetMapping("/completion-rate")
    public ResponseEntity<Map<String, Object>> getTraditionalChineseMedicineGuidanceCompletionRate() {
        System.out.println("收到获取个性化中医指导完成率的请求");

        Map<String, Object> response = new HashMap<>();

        try {
            BigDecimal completionRate = traditionalChineseMedicineGuidanceService.calculateTraditionalChineseMedicineGuidanceCompletionRate();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", completionRate);
            response.put("timestamp", System.currentTimeMillis());

            System.out.println("成功获取个性化中医指导完成率: " + completionRate + "%");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取个性化中医指导完成率时发生异常: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "查询过程中发生错误");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取个性化中医指导完成率详情
     *
     * @return 包含总指导次数、个性化指导次数和完成率的详细信息
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getTraditionalChineseMedicineGuidanceDetails() {
        System.out.println("收到获取个性化中医指导完成率详情的请求");

        Map<String, Object> response = new HashMap<>();

        try {
            TraditionalChineseMedicineGuidanceService.TraditionalChineseMedicineGuidanceDetail details = 
                traditionalChineseMedicineGuidanceService.getAllGuidanceDetails();

            Map<String, Object> data = new HashMap<>();
            data.put("totalGuidanceCount", details.getTotalGuidanceCount());
            data.put("personalizedGuidanceCount", details.getPersonalizedGuidanceCount());
            data.put("completionRate", details.getCompletionRate());

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", data);
            response.put("timestamp", System.currentTimeMillis());

            System.out.println("成功获取个性化中医指导完成率详情");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取个性化中医指导完成率详情时发生异常: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "查询过程中发生错误");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }
}