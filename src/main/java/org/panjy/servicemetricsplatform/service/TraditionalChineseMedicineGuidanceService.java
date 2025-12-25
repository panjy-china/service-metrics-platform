package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.message.UserGuidanceStat;
import org.panjy.servicemetricsplatform.mapper.message.UserGuidanceStatMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 个性化中医指导完成率服务类
 * 用于计算和处理个性化中医指导完成率
 */
@Service
public class TraditionalChineseMedicineGuidanceService {

    @Autowired
    private UserGuidanceStatMapper userGuidanceStatMapper;

    /**
     * 计算总的个性化中医指导完成率
     * 公式：(个性化指导总次数 / 总指导次数) × 100%
     *
     * @return 个性化中医指导完成率
     */
    public BigDecimal calculateTraditionalChineseMedicineGuidanceCompletionRate() {
        // 获取总指导次数和总个性化指导次数
        UserGuidanceStat totalStats = userGuidanceStatMapper.getTotalGuidanceCounts();

        if (totalStats != null) {
            Integer totalCount = totalStats.getGuidanceCount();
            Integer personalizedCount = totalStats.getPersonalizedGuidanceCount();

            // 避免除零错误
            if (totalCount != null && totalCount > 0) {
                // 计算完成率并保留两位小数
                return new BigDecimal(personalizedCount * 100)
                    .divide(new BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP);
            }
        }

        // 如果没有数据或总次数为0，返回0
        return BigDecimal.ZERO;
    }

    /**
     * 计算指定月份的个性化中医指导完成率
     * 公式：(个性化指导总次数 / 总指导次数) × 100%
     *
     * @param targetDate 目标日期
     * @return 指定月份的个性化中医指导完成率
     */
    public BigDecimal calculateTraditionalChineseMedicineGuidanceCompletionRateByMonth(LocalDateTime targetDate) {
        // 获取指定月份的总指导次数和总个性化指导次数
        UserGuidanceStat totalStats = userGuidanceStatMapper.getTotalGuidanceCountsByMonth(targetDate);

        if (totalStats != null) {
            Integer totalCount = totalStats.getGuidanceCount();
            Integer personalizedCount = totalStats.getPersonalizedGuidanceCount();

            // 避免除零错误
            if (totalCount != null && totalCount > 0) {
                // 计算完成率并保留两位小数
                return new BigDecimal(personalizedCount * 100)
                    .divide(new BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP);
            }
        }

        // 如果没有数据或总次数为0，返回0
        return BigDecimal.ZERO;
    }

    /**
     * 获取所有用户的个性化中医指导完成率详情
     *
     * @return 包含总指导次数、个性化指导次数和完成率的详细信息
     */
    public TraditionalChineseMedicineGuidanceDetail getAllGuidanceDetails() {
        // 获取总指导次数和总个性化指导次数
        UserGuidanceStat totalStats = userGuidanceStatMapper.getTotalGuidanceCounts();

        if (totalStats != null) {
            Integer totalCount = totalStats.getGuidanceCount();
            Integer personalizedCount = totalStats.getPersonalizedGuidanceCount();

            // 计算完成率
            BigDecimal completionRate = BigDecimal.ZERO;
            if (totalCount != null && totalCount > 0) {
                completionRate = new BigDecimal(personalizedCount * 100)
                    .divide(new BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP);
            }

            return new TraditionalChineseMedicineGuidanceDetail(
                totalCount != null ? totalCount : 0,
                personalizedCount != null ? personalizedCount : 0,
                completionRate
            );
        }

        // 如果没有数据，返回默认值
        return new TraditionalChineseMedicineGuidanceDetail(0, 0, BigDecimal.ZERO);
    }

    /**
     * 计算指定月份的个性化中医指导完成率及环比增长率
     *
     * @param targetDate 目标日期
     * @return 包含当月个性化中医指导完成率和环比增长率的Map
     */
    public Map<String, Object> calculateGuidanceCompletionRateWithGrowth(LocalDateTime targetDate) {
        // 计算目标月份的个性化中医指导完成率
        BigDecimal currentRate = calculateTraditionalChineseMedicineGuidanceCompletionRateByMonth(targetDate);
        
        // 计算上个月的个性化中医指导完成率
        LocalDateTime previousMonth = targetDate.minusMonths(1);
        BigDecimal previousRate = calculateTraditionalChineseMedicineGuidanceCompletionRateByMonth(previousMonth);
        
        // 计算环比增长率
        BigDecimal growthRate = BigDecimal.ZERO;
        if (previousRate.compareTo(BigDecimal.ZERO) != 0) {
            growthRate = currentRate.subtract(previousRate)
                .multiply(BigDecimal.valueOf(100))
                .divide(previousRate, 2, BigDecimal.ROUND_HALF_UP);
        }
        
        // 构造返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("currentRate", currentRate.toString());  // 不带%的百分比
        result.put("growthRate", growthRate.toString());  // 环比增长率，不带%的百分比
        
        return result;
    }

    /**
     * 个性化中医指导详情类
     */
    public static class TraditionalChineseMedicineGuidanceDetail {
        private int totalGuidanceCount;          // 总指导次数
        private int personalizedGuidanceCount;   // 个性化指导次数
        private BigDecimal completionRate;       // 完成率

        public TraditionalChineseMedicineGuidanceDetail(int totalGuidanceCount, int personalizedGuidanceCount, BigDecimal completionRate) {
            this.totalGuidanceCount = totalGuidanceCount;
            this.personalizedGuidanceCount = personalizedGuidanceCount;
            this.completionRate = completionRate;
        }

        // Getter方法
        public int getTotalGuidanceCount() {
            return totalGuidanceCount;
        }

        public int getPersonalizedGuidanceCount() {
            return personalizedGuidanceCount;
        }

        public BigDecimal getCompletionRate() {
            return completionRate;
        }

        // Setter方法
        public void setTotalGuidanceCount(int totalGuidanceCount) {
            this.totalGuidanceCount = totalGuidanceCount;
        }

        public void setPersonalizedGuidanceCount(int personalizedGuidanceCount) {
            this.personalizedGuidanceCount = personalizedGuidanceCount;
        }

        public void setCompletionRate(BigDecimal completionRate) {
            this.completionRate = completionRate;
        }
    }
}