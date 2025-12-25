package org.panjy.servicemetricsplatform.service.conversionrate;

import org.panjy.servicemetricsplatform.entity.conversionrate.ClientServiceStats;
import org.panjy.servicemetricsplatform.mapper.conversionrate.ClientServiceStatsMapper;
import org.panjy.servicemetricsplatform.mapper.WechatMemberMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户服务统计服务类
 */
@Service
public class ClientServiceStatsService {
    
    @Autowired
    private ClientServiceStatsMapper clientServiceStatsMapper;
    
    @Autowired
    private WechatMemberMapper wechatMemberMapper;
    
    /**
     * 获取满足条件的客户服务统计数据
     * @return 客户服务统计数据列表
     */
    public List<ClientServiceStats> getClientServiceStats() {
        return clientServiceStatsMapper.getClientServiceStats();
    }
    
    /**
     * 获取指定月份满足条件的客户服务统计数据
     * @param targetDate 目标日期
     * @return 客户服务统计数据列表
     */
    public List<ClientServiceStats> getClientServiceStatsByMonth(LocalDateTime targetDate) {
        return clientServiceStatsMapper.getClientServiceStatsByMonth(targetDate);
    }
    
    /**
     * 计算推单成交率
     * 推单成交率 = 满足条件的客户数 / 客户总数
     * @return 推单成交率
     */
    public double calculatePushOrderConversionRate() {
        // 获取满足条件的客户数（即ClientServiceStats结果的数量）
        int qualifiedClientCount = getClientServiceStats().size();
        
        // 获取客户总数
        int totalClientCount = wechatMemberMapper.countDistinctColCltID();
        
        // 避免除零错误
        if (totalClientCount == 0) {
            return 0.0;
        }
        
        // 计算并返回推单成交率
        return (double) qualifiedClientCount / totalClientCount;
    }
    
    /**
     * 计算指定月份的推单成交率
     * 推单成交率 = 满足条件的客户数 / 客户总数
     * @param targetDate 目标日期
     * @return 推单成交率
     */
    public double calculatePushOrderConversionRateByMonth(LocalDateTime targetDate) {
        // 获取满足条件的客户数（即ClientServiceStats结果的数量）
        int qualifiedClientCount = getClientServiceStatsByMonth(targetDate).size();
        
        // 获取客户总数
        int totalClientCount = wechatMemberMapper.countDistinctColCltID();
        
        // 避免除零错误
        if (totalClientCount == 0) {
            return 0.0;
        }
        
        // 计算并返回推单成交率
        return (double) qualifiedClientCount / totalClientCount;
    }
    
    /**
     * 计算指定月份的推单成交率及环比增长率
     * @param targetDate 目标日期
     * @return 包含当月成交率和环比增长率的Map
     */
    public java.util.Map<String, Object> calculatePushOrderConversionRateWithGrowth(LocalDateTime targetDate) {
        // 计算目标月份的成交率
        double currentRate = calculatePushOrderConversionRateByMonth(targetDate);
        
        // 计算上个月的成交率
        LocalDateTime previousMonth = targetDate.minusMonths(1);
        double previousRate = calculatePushOrderConversionRateByMonth(previousMonth);
        
        // 计算环比增长率
        double growthRate = 0.0;
        if (previousRate != 0) {
            growthRate = (currentRate - previousRate) / previousRate;
        }
        
        // 构造返回结果
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("currentRate", String.format("%.2f", currentRate * 100));  // 不带%的百分比
        result.put("growthRate", String.format("%.2f", growthRate * 100));  // 环比增长率，带%的百分比
        
        return result;
    }
}