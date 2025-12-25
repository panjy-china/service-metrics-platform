package org.panjy.servicemetricsplatform.service.call;

import org.panjy.servicemetricsplatform.entity.call.FirstCallRecord;
import org.panjy.servicemetricsplatform.entity.call.FirstCallSummary;
import org.panjy.servicemetricsplatform.mapper.call.FirstCallSummaryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * FirstCallSummary服务类
 * 提供对首通电话摘要记录的业务逻辑处理
 */
@Service
public class FirstCallSummaryService {
    
    @Autowired
    private FirstCallSummaryMapper firstCallSummaryMapper;
    
    /**
     * 查询所有首通电话摘要记录
     * 
     * @return 所有首通电话摘要记录列表
     */
    public List<FirstCallSummary> selectAll() {
        return firstCallSummaryMapper.selectAll();
    }
    
    /**
     * 根据微信ID获取首通记录
     * 
     * @param wechatId 微信ID
     * @return 首通记录列表
     */
    public List<FirstCallRecord> getFirstCallRecordsByWechatId(String wechatId) {
        if (wechatId == null || wechatId.isEmpty()) {
            return new ArrayList<>();
        }
        return firstCallSummaryMapper.selectFirstCallRecordsByWechatId(wechatId);
    }
    
    /**
     * 计算时长达标电话比例
     * 对于通话时长超过十分钟(600秒)的认为时长达标
     * 
     * @return 时长达标电话总数/电话总数的比例
     */
    public double calculateQualifiedRate() {
        List<FirstCallSummary> allRecords = selectAll();
        
        if (allRecords.isEmpty()) {
            return 0.0;
        }
        
        long totalCalls = allRecords.size();
        long qualifiedCalls = allRecords.stream()
                .filter(record -> record.getCallDuration() != null && record.getCallDuration() > 600)
                .count();
        
        return (double) qualifiedCalls / totalCalls;
    }
    
    /**
     * 计算所有首通电话的平均通话时长
     * 
     * @return 平均通话时长（秒）
     */
    public double calculateAverageCallDuration() {
        List<FirstCallSummary> allRecords = selectAll();
        
        if (allRecords.isEmpty()) {
            return 0.0;
        }
        
        // 过滤掉通话时长为空的记录，然后计算平均值
        return allRecords.stream()
                .filter(record -> record.getCallDuration() != null)
                .mapToLong(FirstCallSummary::getCallDuration)
                .average()
                .orElse(0.0);
    }
    
    /**
     * 获取时长达标电话总数和电话总数
     * 
     * @return 包含达标电话数和总电话数的数组，[达标数, 总数]
     */
    public long[] getQualifiedAndTotalCount() {
        List<FirstCallSummary> allRecords = selectAll();
        
        long totalCalls = allRecords.size();
        long qualifiedCalls = allRecords.stream()
                .filter(record -> record.getCallDuration() != null && record.getCallDuration() > 600)
                .count();
        
        return new long[]{qualifiedCalls, totalCalls};
    }
    
    /**
     * 计算指定月份时长达标电话比例及与上个月相比的增长率
     * 对于通话时长超过十分钟(600秒)的认为时长达标
     * 
     * @param date 指定日期（用于确定月份）
     * @return 包含时长达标电话比例和增长率的Map
     */
    public Map<String, Object> calculateQualifiedRateByMonth(LocalDateTime date) {
        // 获取当前月的数据
        List<FirstCallSummary> currentMonthRecords = firstCallSummaryMapper.selectByMonth(date);
        
        // 获取上个月的数据
        LocalDateTime previousMonthDate = date.minusMonths(1);
        List<FirstCallSummary> previousMonthRecords = firstCallSummaryMapper.selectByMonth(previousMonthDate);
        
        // 计算当前月的时长达标电话比例
        double currentRate = 0.0;
        if (!currentMonthRecords.isEmpty()) {
            long currentTotalCalls = currentMonthRecords.size();
            long currentQualifiedCalls = currentMonthRecords.stream()
                    .filter(record -> record.getCallDuration() != null && record.getCallDuration() > 600)
                    .count();
            currentRate = (double) currentQualifiedCalls / currentTotalCalls;
        }
        
        // 计算上个月的时长达标电话比例
        double previousRate = 0.0;
        if (!previousMonthRecords.isEmpty()) {
            long previousTotalCalls = previousMonthRecords.size();
            long previousQualifiedCalls = previousMonthRecords.stream()
                    .filter(record -> record.getCallDuration() != null && record.getCallDuration() > 600)
                    .count();
            previousRate = (double) previousQualifiedCalls / previousTotalCalls;
        }
        
        // 计算增长率
        double growthRate = 0.0;
        if (previousRate != 0) {
            growthRate = (currentRate - previousRate) / previousRate;
        } else if (currentRate > 0) {
            // 如果上个月为0，而当前月大于0，则增长率为无穷大，这里用100%表示显著增长
            growthRate = 1.0;
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("rate", currentRate);
        result.put("growthRate", growthRate);
        return result;
    }
    
    /**
     * 计算指定月份所有首通电话的平均通话时长及与上个月相比的增长率
     * 
     * @param date 指定日期（用于确定月份）
     * @return 包含平均通话时长和增长率的Map
     */
    public Map<String, Object> calculateAverageCallDurationByMonth(LocalDateTime date) {
        // 获取当前月的数据
        List<FirstCallSummary> currentMonthRecords = firstCallSummaryMapper.selectByMonth(date);
        
        // 获取上个月的数据
        LocalDateTime previousMonthDate = date.minusMonths(1);
        List<FirstCallSummary> previousMonthRecords = firstCallSummaryMapper.selectByMonth(previousMonthDate);
        
        // 计算当前月的平均通话时长
        double currentAverage = 0.0;
        if (!currentMonthRecords.isEmpty()) {
            currentAverage = currentMonthRecords.stream()
                    .filter(record -> record.getCallDuration() != null)
                    .mapToLong(FirstCallSummary::getCallDuration)
                    .average()
                    .orElse(0.0);
        }
        
        // 计算上个月的平均通话时长
        double previousAverage = 0.0;
        if (!previousMonthRecords.isEmpty()) {
            previousAverage = previousMonthRecords.stream()
                    .filter(record -> record.getCallDuration() != null)
                    .mapToLong(FirstCallSummary::getCallDuration)
                    .average()
                    .orElse(0.0);
        }
        
        // 计算增长率
        double growthRate = 0.0;
        if (previousAverage != 0) {
            growthRate = (currentAverage - previousAverage) / previousAverage;
        } else if (currentAverage > 0) {
            // 如果上个月为0，而当前月大于0，则增长率为无穷大，这里用100%表示显著增长
            growthRate = 1.0;
        }
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("averageDuration", currentAverage);
        result.put("growthRate", growthRate);
        return result;
    }
    
    /**
     * 获取指定月份时长达标电话总数和电话总数及上个月的对应数据
     * 
     * @param date 指定日期（用于确定月份）
     * @return 包含达标电话数、总电话数以及上个月对应数据的Map
     */
    public Map<String, Object> getQualifiedAndTotalCountByMonth(LocalDateTime date) {
        // 获取当前月的数据
        List<FirstCallSummary> currentMonthRecords = firstCallSummaryMapper.selectByMonth(date);
        
        // 获取上个月的数据
        LocalDateTime previousMonthDate = date.minusMonths(1);
        List<FirstCallSummary> previousMonthRecords = firstCallSummaryMapper.selectByMonth(previousMonthDate);
        
        // 计算当前月的数据
        long currentTotalCalls = currentMonthRecords.size();
        long currentQualifiedCalls = currentMonthRecords.stream()
                .filter(record -> record.getCallDuration() != null && record.getCallDuration() > 600)
                .count();
        
        // 计算上个月的数据
        long previousTotalCalls = previousMonthRecords.size();
        long previousQualifiedCalls = previousMonthRecords.stream()
                .filter(record -> record.getCallDuration() != null && record.getCallDuration() > 600)
                .count();
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("qualifiedCount", currentQualifiedCalls);
        result.put("totalCount", currentTotalCalls);
        result.put("previousQualifiedCount", previousQualifiedCalls);
        result.put("previousTotalCount", previousTotalCalls);
        return result;
    }
}