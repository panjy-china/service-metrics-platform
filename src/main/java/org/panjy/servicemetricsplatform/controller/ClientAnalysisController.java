package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.ClientAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 客户分析控制器
 * 
 * @author System Generated
 */
@RestController
@RequestMapping("/api/client-analysis")
public class ClientAnalysisController {

    @Autowired
    private ClientAnalysisService clientAnalysisService;

    /**
     * 获取客户年龄分布数据
     * 
     * @return 年龄分布数据列表
     */
    @GetMapping("/age-distribution")
    public List<Map<String, Object>> getAgeDistribution() {
        return clientAnalysisService.getAgeDistribution();
    }
}