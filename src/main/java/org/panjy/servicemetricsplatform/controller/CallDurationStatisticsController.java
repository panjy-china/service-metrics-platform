package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.CallDurationStatistics;
import org.panjy.servicemetricsplatform.service.CallDurationStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通话时长统计控制器
 * 提供通话时长分布统计的查询接口
 */
@RestController
@RequestMapping("/api/call-duration-statistics")
public class CallDurationStatisticsController {
    
    @Autowired
    private CallDurationStatisticsService callDurationStatisticsService;
    
    /**
     * 获取不同通话时长区间的记录数量
     * 
     * @return 通话时长统计列表
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllCallDurationStatistics() {
        System.out.println("收到获取通话时长统计的请求");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<CallDurationStatistics> statistics = callDurationStatisticsService.getCallDurationStatistics();
            
            response.put("success", true);
            response.put("message", "查询成功");
            response.put("data", statistics);
            
            System.out.println("成功获取通话时长统计，共" + statistics.size() + "条记录");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("获取通话时长统计时发生异常: " + e.getMessage());
            e.printStackTrace();
            
            response.put("success", false);
            response.put("error", "查询过程中发生错误");
            response.put("message", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}