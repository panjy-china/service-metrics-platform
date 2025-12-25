package org.panjy.servicemetricsplatform.service.call;

import org.panjy.servicemetricsplatform.entity.call.CallDurationStatistics;
import org.panjy.servicemetricsplatform.mapper.call.CallDurationStatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通话时长统计服务类
 */
@Service
public class CallDurationStatisticsService {
    
    @Autowired
    private CallDurationStatisticsMapper callDurationStatisticsMapper;
    
    /**
     * 获取不同通话时长区间的记录数量
     * 
     * @return 通话时长统计列表
     */
    public List<CallDurationStatistics> getCallDurationStatistics() {
        return callDurationStatisticsMapper.selectCallDurationStatistics();
    }
    
    /**
     * 获取指定月份不同通话时长区间的记录数量
     * 
     * @param date 指定日期（用于确定月份）
     * @return 通话时长统计列表
     */
    public List<CallDurationStatistics> getCallDurationStatisticsByMonth(LocalDateTime date) {
        return callDurationStatisticsMapper.selectCallDurationStatisticsByMonth(date);
    }
    
    /**
     * 获取指定月份不同通话时长区间的记录数量及环比增长率
     * 
     * @param date 指定日期（用于确定月份）
     * @return 包含当前月统计数据和环比增长率的Map
     */
    public java.util.Map<String, Object> getCallDurationStatisticsWithGrowthByMonth(LocalDateTime date) {
        // 获取当前月的数据
        List<CallDurationStatistics> currentMonthStats = getCallDurationStatisticsByMonth(date);
        
        // 获取上个月的数据
        LocalDateTime previousMonthDate = date.minusMonths(1);
        List<CallDurationStatistics> previousMonthStats = getCallDurationStatisticsByMonth(previousMonthDate);
        
        // 计算环比增长率
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        result.put("currentStats", currentMonthStats);
        
        // 计算总记录数的增长率
        long currentTotal = currentMonthStats.stream().mapToLong(CallDurationStatistics::getRecordCount).sum();
        long previousTotal = previousMonthStats.stream().mapToLong(CallDurationStatistics::getRecordCount).sum();
        
        double growthRate = 0.0;
        if (previousTotal != 0) {
            growthRate = (double) (currentTotal - previousTotal) / previousTotal;
        } else if (currentTotal > 0) {
            growthRate = 1.0; // 如果上个月为0，而当前月大于0，则增长率为100%
        }
        
        result.put("growthRate", growthRate);
        return result;
    }
}