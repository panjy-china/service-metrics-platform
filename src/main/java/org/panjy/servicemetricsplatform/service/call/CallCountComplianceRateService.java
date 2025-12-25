package org.panjy.servicemetricsplatform.service.call;

import org.panjy.servicemetricsplatform.mapper.call.TblTjOutCallMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 通话次数达标率服务类
 * 用于计算四次通话达标率和六次通话达标率
 */
@Service
public class CallCountComplianceRateService {
    
    @Autowired
    private TblTjOutCallMapper tblTjOutCallMapper;
    
    /**
     * 计算四次通话达标率
     * 达标率 = 通话次数>=4的记录数 / 总记录数 * 100%
     * 
     * @return 四次通话达标率（百分比）
     */
    public double calculateFourCallComplianceRate() {
        // 获取所有用户的通话次数列表
        List<Integer> callCounts = tblTjOutCallMapper.selectAllCallCounts();
        
        if (callCounts.isEmpty()) {
            return 0.0;
        }
        
        // 计算总记录数
        int totalCount = callCounts.size();
        
        // 计算通话次数>=4的记录数
        long compliantCount = callCounts.stream()
                .filter(count -> count >= 4)
                .count();
        
        // 计算达标率（百分比）
        return totalCount > 0 ? (double) compliantCount / totalCount * 100 : 0.0;
    }
    
    /**
     * 计算六次通话达标率
     * 达标率 = 通话次数>=6的记录数 / 总记录数 * 100%
     * 
     * @return 六次通话达标率（百分比）
     */
    public double calculateSixCallComplianceRate() {
        // 获取所有用户的通话次数列表
        List<Integer> callCounts = tblTjOutCallMapper.selectAllCallCounts();
        
        if (callCounts.isEmpty()) {
            return 0.0;
        }
        
        // 计算总记录数
        int totalCount = callCounts.size();
        
        // 计算通话次数>=6的记录数
        long compliantCount = callCounts.stream()
                .filter(count -> count >= 6)
                .count();
        
        // 计算达标率（百分比）
        return totalCount > 0 ? (double) compliantCount / totalCount * 100 : 0.0;
    }
    
    /**
     * 获取通话次数统计信息
     * 
     * @return 包含总记录数、四次通话达标数、六次通话达标数的数组
     */
    public long[] getCallCountStatistics() {
        // 获取所有用户的通话次数列表
        List<Integer> callCounts = tblTjOutCallMapper.selectAllCallCounts();
        
        if (callCounts.isEmpty()) {
            return new long[]{0, 0, 0}; // totalCount, fourCompliantCount, sixCompliantCount
        }
        
        // 计算总记录数
        long totalCount = callCounts.size();
        
        // 计算通话次数>=4的记录数
        long fourCompliantCount = callCounts.stream()
                .filter(count -> count >= 4)
                .count();
        
        // 计算通话次数>=6的记录数
        long sixCompliantCount = callCounts.stream()
                .filter(count -> count >= 6)
                .count();
        
        return new long[]{totalCount, fourCompliantCount, sixCompliantCount};
    }
    
    /**
     * 计算指定月份的四次通话达标率
     * 达标率 = 通话次数>=4的记录数 / 总记录数 * 100%
     * 
     * @param yearMonth 指定的年月 (格式: yyyy-MM)
     * @return 四次通话达标率（百分比）
     */
    public double calculateFourCallComplianceRateByMonth(String yearMonth) {
        // 获取指定月份用户的通话次数列表
        List<Integer> callCounts = tblTjOutCallMapper.selectCallCountsByMonth(yearMonth);
        
        if (callCounts.isEmpty()) {
            return 0.0;
        }
        
        // 计算总记录数
        int totalCount = callCounts.size();
        
        // 计算通话次数>=4的记录数
        long compliantCount = callCounts.stream()
                .filter(count -> count >= 4)
                .count();
        
        // 计算达标率（百分比）
        return totalCount > 0 ? (double) compliantCount / totalCount * 100 : 0.0;
    }
    
    /**
     * 计算指定月份的六次通话达标率
     * 达标率 = 通话次数>=6的记录数 / 总记录数 * 100%
     * 
     * @param yearMonth 指定的年月 (格式: yyyy-MM)
     * @return 六次通话达标率（百分比）
     */
    public double calculateSixCallComplianceRateByMonth(String yearMonth) {
        // 获取指定月份用户的通话次数列表
        List<Integer> callCounts = tblTjOutCallMapper.selectCallCountsByMonth(yearMonth);
        
        if (callCounts.isEmpty()) {
            return 0.0;
        }
        
        // 计算总记录数
        int totalCount = callCounts.size();
        
        // 计算通话次数>=6的记录数
        long compliantCount = callCounts.stream()
                .filter(count -> count >= 6)
                .count();
        
        // 计算达标率（百分比）
        return totalCount > 0 ? (double) compliantCount / totalCount * 100 : 0.0;
    }
    
    /**
     * 计算指定月份的四次通话达标率环比增长率
     * 
     * @param yearMonth 指定的年月 (格式: yyyy-MM)
     * @return 环比增长率（百分比）
     */
    public double calculateFourCallComplianceRateGrowthByMonth(String yearMonth) {
        // 计算当前月份的达标率
        double currentRate = calculateFourCallComplianceRateByMonth(yearMonth);
        
        // 计算上个月的达标率
        String previousMonth = getPreviousMonth(yearMonth);
        double previousRate = calculateFourCallComplianceRateByMonth(previousMonth);
        
        // 计算环比增长率
        if (previousRate == 0) {
            return currentRate > 0 ? 100.0 : 0.0;
        }
        
        return ((currentRate - previousRate) / previousRate) * 100;
    }
    
    /**
     * 计算指定月份的六次通话达标率环比增长率
     * 
     * @param yearMonth 指定的年月 (格式: yyyy-MM)
     * @return 环比增长率（百分比）
     */
    public double calculateSixCallComplianceRateGrowthByMonth(String yearMonth) {
        // 计算当前月份的达标率
        double currentRate = calculateSixCallComplianceRateByMonth(yearMonth);
        
        // 计算上个月的达标率
        String previousMonth = getPreviousMonth(yearMonth);
        double previousRate = calculateSixCallComplianceRateByMonth(previousMonth);
        
        // 计算环比增长率
        if (previousRate == 0) {
            return currentRate > 0 ? 100.0 : 0.0;
        }
        
        return ((currentRate - previousRate) / previousRate) * 100;
    }
    
    /**
     * 获取上一个月的年月字符串
     * 
     * @param yearMonth 当前年月 (格式: yyyy-MM)
     * @return 上一个月的年月字符串
     */
    private String getPreviousMonth(String yearMonth) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(yearMonth + "-01");
            java.time.LocalDate previousMonth = date.minusMonths(1);
            return previousMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            // 如果解析失败，返回原字符串减去1个月（简单处理）
            return yearMonth;
        }
    }
}