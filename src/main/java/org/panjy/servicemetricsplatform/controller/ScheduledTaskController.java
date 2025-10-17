package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.util.UnifiedScheduledTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 定时任务控制器
 * 提供手动触发定时任务的API接口
 */
@RestController
@RequestMapping("/api/scheduled-tasks")
public class ScheduledTaskController {

    @Autowired
    private UnifiedScheduledTask unifiedScheduledTask;

    /**
     * 执行指定日期的所有定时任务
     *
     * @param dateStr 指定日期字符串 (格式: yyyy-MM-dd)
     * @return 执行结果
     */
    @PostMapping("/execute-all/{dateStr}")
    public ResponseEntity<Map<String, Object>> executeAllScheduledTasks(@PathVariable String dateStr) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 解析日期参数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            
            // 执行所有定时任务
            unifiedScheduledTask.executeAllScheduledTasksInOrder(date);
            
            response.put("success", true);
            response.put("message", "所有定时任务执行完成");
            response.put("date", dateStr);
            response.put("description", "按顺序执行了以下任务：\n" +
                    "1. 地址分析任务\n" +
                    "2. 地址用户映射任务\n" +
                    "3. 饮食指导分析任务\n" +
                    "4. 基于日期的对话分析任务\n" +
                    "5. 三餐打卡分析任务\n" +
                    "6. 服务时间处理任务\n" +
                    "7. 用户首次反馈统计任务");
            
            return ResponseEntity.ok(response);
        } catch (ParseException e) {
            response.put("success", false);
            response.put("message", "日期格式错误，请使用 yyyy-MM-dd 格式");
            response.put("date", dateStr);
            response.put("errorCode", "INVALID_DATE_FORMAT");
            
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "执行定时任务时发生错误: " + e.getMessage());
            response.put("date", dateStr);
            response.put("errorCode", "EXECUTION_ERROR");
            
            return ResponseEntity.status(500).body(response);
        }
    }
}