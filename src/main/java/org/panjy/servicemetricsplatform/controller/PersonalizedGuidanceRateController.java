package org.panjy.servicemetricsplatform.controller;

import org.panjy.servicemetricsplatform.entity.CallStatistics;
import org.panjy.servicemetricsplatform.service.PersonalizedGuidanceRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 个性化指导率控制器
 * 提供个性化指导触达率的API接口
 */
@RestController
@RequestMapping("/api/personalized-guidance-rate")
@CrossOrigin(origins = "*") // 允许跨域请求
public class PersonalizedGuidanceRateController {

    @Autowired
    private PersonalizedGuidanceRateService personalizedGuidanceRateService;

    /**
     * 获取所有用户的个性化指导触达率
     *
     * @return 包含个性化指导触达率的通话统计信息列表
     */
    @GetMapping("/all")
    public List<CallStatistics> getAllPersonalizedGuidanceRates() {
        return personalizedGuidanceRateService.calculatePersonalizedGuidanceRates();
    }

    /**
     * 根据微信ID获取个性化指导触达率
     *
     * @param wechatId 微信ID
     * @return 个性化指导触达率
     */
    @GetMapping("/{wechatId}")
    public Double getPersonalizedGuidanceRateByWechatId(@PathVariable String wechatId) {
        return personalizedGuidanceRateService.getPersonalizedGuidanceRateByWechatId(wechatId);
    }

    /**
     * 获取所有用户的个性化指导触达率映射
     *
     * @return 微信ID和对应的个性化指导触达率映射
     */
    @GetMapping("/map")
    public Map<String, Double> getAllPersonalizedGuidanceRateMap() {
        return personalizedGuidanceRateService.getAllPersonalizedGuidanceRates();
    }
}