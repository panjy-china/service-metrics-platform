package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.task.HealthProfileAnalysisScheduledTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 健康画像分析定时任务控制器
 * 提供手动触发健康画像分析任务的接口
 */
@RestController
@RequestMapping("/api/health-profile-analysis-task")
public class HealthProfileAnalysisTaskController {

    @Autowired
    private HealthProfileAnalysisScheduledTask healthProfileAnalysisScheduledTask;

    /**
     * 手动执行健康画像分析任务
     * 
     * @return 执行结果
     */
    @PostMapping("/execute")
    public ResponseEntity<String> executeHealthProfileAnalysisTask() {
        try {
            healthProfileAnalysisScheduledTask.executeHealthProfileAnalysisTask();
            return ResponseEntity.ok("健康画像分析任务执行成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("健康画像分析任务执行失败: " + e.getMessage());
        }
    }

    /**
     * 获取健康画像分析任务状态
     * 
     * @return 任务状态信息
     */
    @GetMapping("/status")
    public ResponseEntity<String> getHealthProfileAnalysisTaskStatus() {
        // 这里可以返回任务状态信息，当前简单返回一个确认信息
        return ResponseEntity.ok("健康画像分析定时任务服务正常");
    }
}