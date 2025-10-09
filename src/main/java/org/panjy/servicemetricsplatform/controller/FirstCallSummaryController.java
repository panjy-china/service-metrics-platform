package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.FirstCallSummary;
import org.panjy.servicemetricsplatform.service.FirstCallSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * FirstCallSummary控制器
 * 提供首通电话摘要记录的HTTP接口
 */
@RestController
@RequestMapping("/api/first-call-summary")
public class FirstCallSummaryController {
    
    @Autowired
    private FirstCallSummaryService firstCallSummaryService;
    
    /**
     * 获取所有首通电话摘要记录
     * 
     * @return 所有首通电话摘要记录列表
     */
    @GetMapping("/all")
    public List<FirstCallSummary> getAllFirstCallSummaries() {
        return firstCallSummaryService.selectAll();
    }
    
    /**
     * 获取时长达标电话比例
     * 
     * @return 时长达标电话比例
     */
    @GetMapping("/qualified-rate")
    public double getQualifiedRate() {
        return firstCallSummaryService.calculateQualifiedRate();
    }
    
    /**
     * 获取所有首通电话的平均通话时长
     * 
     * @return 平均通话时长（秒）
     */
    @GetMapping("/average-duration")
    public double getAverageCallDuration() {
        return firstCallSummaryService.calculateAverageCallDuration();
    }
    
    /**
     * 获取时长达标电话总数和电话总数
     * 
     * @return 包含达标电话数和总电话数的数组，[达标数, 总数]
     */
    @GetMapping("/counts")
    public long[] getQualifiedAndTotalCount() {
        return firstCallSummaryService.getQualifiedAndTotalCount();
    }
}