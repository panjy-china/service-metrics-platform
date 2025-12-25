package org.panjy.servicemetricsplatform.service.mealcomletion;

import org.panjy.servicemetricsplatform.entity.mealcomletion.MealCheckinCompletionRate;
import org.panjy.servicemetricsplatform.mapper.mealcomletion.MealCheckinCompletionRateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    
    /**
     * 获取指定月份不同时间段的餐食打卡完成率
     * 
     * @param targetDate 目标日期
     * @return 餐食打卡完成率列表
     */
    public List<MealCheckinCompletionRate> getMealCheckinCompletionRatesByMonth(LocalDateTime targetDate) {
        return mealCheckinCompletionRateMapper.selectMealCheckinCompletionRatesByMonth(targetDate);
    }
    
    /**
     * 计算指定月份的餐食打卡完成率及环比增长率
     * 
     * @param targetDate 目标日期
     * @return 包含当月完成率和环比增长率的Map
     */
    public java.util.Map<String, Object> calculateMealCheckinCompletionRateWithGrowth(LocalDateTime targetDate) {
        // 获取当前月的数据
        List<MealCheckinCompletionRate> currentMonthRates = getMealCheckinCompletionRatesByMonth(targetDate);
        
        // 获取上个月的数据
        LocalDateTime previousMonthDate = targetDate.minusMonths(1);
        List<MealCheckinCompletionRate> previousMonthRates = getMealCheckinCompletionRatesByMonth(previousMonthDate);
        
        // 计算环比增长率
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        // 为每个时间段计算增长率
        if (currentMonthRates != null && !currentMonthRates.isEmpty()) {
            for (MealCheckinCompletionRate currentRate : currentMonthRates) {
                String rangeName = currentRate.getRangeName();
                Double currentCompletionRate = currentRate.getCompletionRate();
                
                // 查找上个月对应时间段的数据
                Double previousCompletionRate = 0.0;
                if (previousMonthRates != null && !previousMonthRates.isEmpty()) {
                    for (MealCheckinCompletionRate previousRate : previousMonthRates) {
                        if (rangeName.equals(previousRate.getRangeName())) {
                            previousCompletionRate = previousRate.getCompletionRate();
                            break;
                        }
                    }
                }
                
                // 计算增长率
                double growthRate = 0.0;
                if (previousCompletionRate != 0) {
                    growthRate = (currentCompletionRate - previousCompletionRate) / previousCompletionRate;
                } else if (currentCompletionRate > 0) {
                    growthRate = 1.0; // 如果上个月为0，而当前月大于0，则增长率为100%
                }
                
                // 存储结果
                java.util.Map<String, Object> rangeResult = new java.util.HashMap<>();
                rangeResult.put("currentRate", String.format("%.2f", currentCompletionRate * 100));
                rangeResult.put("growthRate", String.format("%.2f", growthRate * 100));
                result.put(rangeName, rangeResult);
            }
        }
        
        return result;
    }
}