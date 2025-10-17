package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.OrderCallTimeDiff;
import org.panjy.servicemetricsplatform.entity.PersonalizedGuidanceCompletionRate;
import org.panjy.servicemetricsplatform.mapper.OrderCallTimeDiffMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * OrderCallTimeDiff服务类
 * 用于处理订单与首电时间差相关的业务逻辑
 */
@Service
public class OrderCallTimeDiffService {

    private static final Logger logger = Logger.getLogger(OrderCallTimeDiffService.class.getName());

    @Autowired
    private OrderCallTimeDiffMapper orderCallTimeDiffMapper;

    /**
     * 计算个性化中医指导完成率
     * 完成率 = over_15_min_count / over_1_min_count
     *
     * @return 个性化中医指导完成率，如果分母为0则返回0
     */
    public BigDecimal calculatePersonalizedGuidanceCompletionRate() {
        try {
            // 获取通话时长差异统计数据
            PersonalizedGuidanceCompletionRate timeDiff = orderCallTimeDiffMapper.selectOrderCallTimeDiff();
            
            // 检查数据是否存在
            if (timeDiff == null) {
                logger.warning("未获取到通话时长差异统计数据，返回默认值0");
                return BigDecimal.ZERO;
            }

            // 获取超过1分钟和超过15分钟的通话次数
            Integer over1MinCount = timeDiff.getOver1MinCount();
            Integer over15MinCount = timeDiff.getOver15MinCount();

            // 检查数据是否有效
            if (over1MinCount == null || over15MinCount == null) {
                logger.warning("通话时长统计数据存在空值，over1MinCount=" + over1MinCount + ", over15MinCount=" + over15MinCount + "，返回默认值0");
                return BigDecimal.ZERO;
            }

            if (over1MinCount == 0) {
                logger.info("分母为0，无法计算完成率，返回默认值0");
                return BigDecimal.ZERO;
            }

            // 计算完成率
            // 使用BigDecimal确保精度
            BigDecimal over15MinCountDecimal = new BigDecimal(over15MinCount);
            BigDecimal over1MinCountDecimal = new BigDecimal(over1MinCount);

            // 完成率 = over_15_min_count / over_1_min_count
            BigDecimal completionRate = over15MinCountDecimal.divide(over1MinCountDecimal, 6, RoundingMode.HALF_UP);

            logger.info("成功计算个性化中医指导完成率: " + completionRate);
            return completionRate;
        } catch (Exception e) {
            logger.severe("计算个性化中医指导完成率时发生异常: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 计算首电完成平均用时
     * 过滤条件：
     * 1. 去除时间为负数的记录
     * 2. 去除秒数大于432000的记录（5天）
     * 计算公式：符合条件的用时和 / 符合条件的总个数
     * 单位转换：秒 -> 天
     *
     * @return 首电完成平均用时（天），如果无符合条件的数据则返回0
     */
    public BigDecimal calculateAverageCallCompletionTimeInDays() {
        try {
            // 获取所有订单与首电时间差数据
            List<OrderCallTimeDiff> allTimeDiffs = orderCallTimeDiffMapper.selectAllOrderCallTimeDiffs();
            logger.info("获取到订单与首电时间差数据记录数: " + allTimeDiffs.size());

            // 过滤数据：去除时间为负数的记录和秒数大于432000的记录
            List<OrderCallTimeDiff> filteredTimeDiffs = allTimeDiffs.stream()
                    .filter(diff -> diff.getDiffSeconds() != null)
                    .filter(diff -> diff.getDiffSeconds() >= 0)           // 去除负数
                    .filter(diff -> diff.getDiffSeconds() <= 432000)      // 去除大于5天的记录
                    .collect(Collectors.toList());
            
            logger.info("过滤后符合条件的记录数: " + filteredTimeDiffs.size());

            // 如果没有符合条件的数据，返回0
            if (filteredTimeDiffs.isEmpty()) {
                logger.info("没有符合条件的数据，返回默认值0");
                return BigDecimal.ZERO;
            }

            // 计算符合条件的用时总和
            long totalSeconds = filteredTimeDiffs.stream()
                    .mapToLong(OrderCallTimeDiff::getDiffSeconds)
                    .sum();
            
            logger.info("符合条件的用时总和(秒): " + totalSeconds);

            // 计算平均用时（秒）
            double averageSeconds = (double) totalSeconds / filteredTimeDiffs.size();

            // 转换为天（1天 = 86400秒）
            double averageDays = averageSeconds / 86400.0;

            // 返回结果，保留6位小数
            BigDecimal result = BigDecimal.valueOf(averageDays).setScale(6, BigDecimal.ROUND_HALF_UP);
            logger.info("计算得到的首电完成平均用时(天): " + result);
            return result;
        } catch (Exception e) {
            logger.severe("计算首电完成平均用时时发生异常: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 获取符合条件的订单与首电时间差详情
     * 过滤条件：
     * 1. 去除时间为负数的记录
     * 2. 去除秒数大于432000的记录（5天）
     *
     * @return 符合条件的订单与首电时间差列表
     */
    public List<OrderCallTimeDiff> getFilteredOrderCallTimeDiffs() {
        try {
            // 获取所有订单与首电时间差数据
            List<OrderCallTimeDiff> allTimeDiffs = orderCallTimeDiffMapper.selectAllOrderCallTimeDiffs();
            logger.info("获取到订单与首电时间差数据记录数: " + allTimeDiffs.size());

            // 过滤数据：去除时间为负数的记录和秒数大于432000的记录
            List<OrderCallTimeDiff> filteredTimeDiffs = allTimeDiffs.stream()
                    .filter(diff -> diff.getDiffSeconds() != null)
                    .filter(diff -> diff.getDiffSeconds() >= 0)           // 去除负数
                    .filter(diff -> diff.getDiffSeconds() <= 432000)      // 去除大于5天的记录
                    .collect(Collectors.toList());
            
            logger.info("过滤后符合条件的记录数: " + filteredTimeDiffs.size());
            return filteredTimeDiffs;
        } catch (Exception e) {
            logger.severe("获取过滤后的订单与首电时间差数据时发生异常: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * 获取统计数据
     *
     * @return 包含总记录数、符合条件记录数、平均用时（天）的统计信息
     */
    public TimeDiffStatistics getStatistics() {
        try {
            // 获取所有订单与首电时间差数据
            List<OrderCallTimeDiff> allTimeDiffs = orderCallTimeDiffMapper.selectAllOrderCallTimeDiffs();
            logger.info("获取到订单与首电时间差数据记录数: " + allTimeDiffs.size());

            // 总记录数
            int totalCount = allTimeDiffs.size();

            // 过滤数据：去除时间为负数的记录和秒数大于432000的记录
            List<OrderCallTimeDiff> filteredTimeDiffs = allTimeDiffs.stream()
                    .filter(diff -> diff.getDiffSeconds() != null)
                    .filter(diff -> diff.getDiffSeconds() >= 0)           // 去除负数
                    .filter(diff -> diff.getDiffSeconds() <= 432000)      // 去除大于5天的记录
                    .collect(Collectors.toList());
            
            logger.info("过滤后符合条件的记录数: " + filteredTimeDiffs.size());

            // 符合条件记录数
            int filteredCount = filteredTimeDiffs.size();

            // 计算平均用时（天）
            BigDecimal averageTimeInDays = BigDecimal.ZERO;
            if (filteredCount > 0) {
                // 计算符合条件的用时总和
                long totalSeconds = filteredTimeDiffs.stream()
                        .mapToLong(OrderCallTimeDiff::getDiffSeconds)
                        .sum();
                
                logger.info("符合条件的用时总和(秒): " + totalSeconds);

                // 计算平均用时（秒）
                double averageSeconds = (double) totalSeconds / filteredCount;

                // 转换为天（1天 = 86400秒）
                double averageDays = averageSeconds / 86400.0;

                // 返回结果，保留6位小数
                averageTimeInDays = BigDecimal.valueOf(averageDays).setScale(6, BigDecimal.ROUND_HALF_UP);
            }
            
            TimeDiffStatistics statistics = new TimeDiffStatistics(totalCount, filteredCount, averageTimeInDays);
            logger.info("生成统计数据: " + statistics.toString());
            return statistics;
        } catch (Exception e) {
            logger.severe("获取统计数据时发生异常: " + e.getMessage());
            return new TimeDiffStatistics(0, 0, BigDecimal.ZERO);
        }
    }

    /**
     * 时间差统计信息类
     */
    public static class TimeDiffStatistics {
        private int totalCount;           // 总记录数
        private int filteredCount;        // 符合条件记录数
        private BigDecimal averageTime;   // 平均用时（天）

        public TimeDiffStatistics(int totalCount, int filteredCount, BigDecimal averageTime) {
            this.totalCount = totalCount;
            this.filteredCount = filteredCount;
            this.averageTime = averageTime;
        }

        // Getter方法
        public int getTotalCount() {
            return totalCount;
        }

        public int getFilteredCount() {
            return filteredCount;
        }

        public BigDecimal getAverageTime() {
            return averageTime;
        }

        // Setter方法
        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        public void setFilteredCount(int filteredCount) {
            this.filteredCount = filteredCount;
        }

        public void setAverageTime(BigDecimal averageTime) {
            this.averageTime = averageTime;
        }

        @Override
        public String toString() {
            return "TimeDiffStatistics{" +
                    "totalCount=" + totalCount +
                    ", filteredCount=" + filteredCount +
                    ", averageTime=" + averageTime +
                    '}';
        }
    }
}