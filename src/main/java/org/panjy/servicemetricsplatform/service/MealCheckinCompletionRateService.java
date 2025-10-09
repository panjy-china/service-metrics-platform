package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.MealCheckinCompletionRate;
import org.panjy.servicemetricsplatform.mapper.MealCheckinCompletionRateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 餐食打卡完成率服务类
 */
@Service
public class MealCheckinCompletionRateService {
    
    @Autowired
    private MealCheckinCompletionRateMapper mealCheckinCompletionRateMapper;
    
    /**
     * 获取不同时间段的餐食打卡完成率
     * 
     * @return 餐食打卡完成率列表
     */
    public List<MealCheckinCompletionRate> getMealCheckinCompletionRates() {
        return mealCheckinCompletionRateMapper.selectMealCheckinCompletionRates();
    }
}