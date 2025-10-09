package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.service.UserGuidanceStatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 饮食指导触达率控制器
 */
@RestController
@RequestMapping("/api/dietary-guidance")
public class DietaryGuidanceReachRateController {

    @Autowired
    private UserGuidanceStatService userGuidanceStatService;

    /**
     * 计算总的饮食指导触达率
     * 公式：(个性化指导总次数 / 总指导次数) × 100%
     *
     * @return 饮食指导触达率
     */
    @GetMapping("/reach-rate")
    public BigDecimal getTotalGuidanceReachRate() {
        return userGuidanceStatService.calculateTotalGuidanceReachRate();
    }
}