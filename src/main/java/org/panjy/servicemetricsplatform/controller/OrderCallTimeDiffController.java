package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.OrderCallTimeDiff;
import org.panjy.servicemetricsplatform.entity.PersonalizedGuidanceCompletionRate;
import org.panjy.servicemetricsplatform.service.OrderCallTimeDiffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OrderCallTimeDiff控制器
 * 提供订单与首电时间差相关的API接口
 */
@RestController
@RequestMapping("/api/order-call-time-diff")
@CrossOrigin(origins = "*") // 允许跨域请求
public class OrderCallTimeDiffController {

    @Autowired
    private OrderCallTimeDiffService orderCallTimeDiffService;

    /**
     * 获取首电完成平均用时
     *
     * @return 首电完成平均用时（天）
     */
    @GetMapping("/average-time")
    public ResponseEntity<Map<String, Object>> getAverageCallCompletionTime() {
        System.out.println("收到获取首电完成平均用时的请求");

        Map<String, Object> response = new HashMap<>();

        try {
            BigDecimal averageTime = orderCallTimeDiffService.calculateAverageCallCompletionTimeInDays();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", averageTime);
            response.put("timestamp", System.currentTimeMillis());

            System.out.println("成功获取首电完成平均用时: " + averageTime + " 天");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取首电完成平均用时时发生异常: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "查询过程中发生错误");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取个性化中医指导完成率
     *
     * @return 个性化中医指导完成率
     */
    @GetMapping("/personalized-guidance-completion-rate")
    public ResponseEntity<Map<String, Object>> getPersonalizedGuidanceCompletionRate() {
        System.out.println("收到获取个性化中医指导完成率的请求");

        Map<String, Object> response = new HashMap<>();

        try {
            BigDecimal completionRate = orderCallTimeDiffService.calculatePersonalizedGuidanceCompletionRate();

            Map<String, Object> data = new HashMap<>();
            data.put("completionRate", completionRate);

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", data);
            response.put("timestamp", System.currentTimeMillis());

            System.out.println("成功获取个性化中医指导完成率: " + completionRate);
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
     * 获取符合条件的订单与首电时间差详情
     *
     * @return 符合条件的订单与首电时间差列表
     */
    @GetMapping("/details")
    public ResponseEntity<Map<String, Object>> getOrderCallTimeDiffDetails() {
        System.out.println("收到获取订单与首电时间差详情的请求");

        Map<String, Object> response = new HashMap<>();

        try {
            List<OrderCallTimeDiff> details = orderCallTimeDiffService.getFilteredOrderCallTimeDiffs();

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", details);
            response.put("timestamp", System.currentTimeMillis());

            System.out.println("成功获取订单与首电时间差详情，共" + details.size() + "条记录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取订单与首电时间差详情时发生异常: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "查询过程中发生错误");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 获取统计数据
     *
     * @return 包含总记录数、符合条件记录数、平均用时（天）的统计信息
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        System.out.println("收到获取统计数据的请求");

        Map<String, Object> response = new HashMap<>();

        try {
            OrderCallTimeDiffService.TimeDiffStatistics statistics = orderCallTimeDiffService.getStatistics();

            Map<String, Object> data = new HashMap<>();
            data.put("totalCount", statistics.getTotalCount());
            data.put("filteredCount", statistics.getFilteredCount());
            data.put("averageTimeInDays", statistics.getAverageTime());

            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", data);
            response.put("timestamp", System.currentTimeMillis());

            System.out.println("成功获取统计数据: " + statistics.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取统计数据时发生异常: " + e.getMessage());
            e.printStackTrace();

            response.put("success", false);
            response.put("message", "查询过程中发生错误");
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(500).body(response);
        }
    }
}