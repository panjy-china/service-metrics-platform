package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.OrderCallTimeDiff;
import org.panjy.servicemetricsplatform.mapper.OrderCallTimeDiffMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * OrderCallTimeDiff服务类
 * 用于处理订单与首电时间差相关的业务逻辑
 */
@Service
public class OrderCallTimeDiffService {

    @Autowired
    private OrderCallTimeDiffMapper orderCallTimeDiffMapper;

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
        // 获取所有订单与首电时间差数据
        List<OrderCallTimeDiff> allTimeDiffs = orderCallTimeDiffMapper.selectAllOrderCallTimeDiffs();

        // 过滤数据：去除时间为负数的记录和秒数大于432000的记录
        List<OrderCallTimeDiff> filteredTimeDiffs = allTimeDiffs.stream()
                .filter(diff -> diff.getDiffSeconds() != null)
                .filter(diff -> diff.getDiffSeconds() >= 0)           // 去除负数
                .filter(diff -> diff.getDiffSeconds() <= 432000)      // 去除大于5天的记录
                .collect(Collectors.toList());

        // 如果没有符合条件的数据，返回0
        if (filteredTimeDiffs.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 计算符合条件的用时总和
        long totalSeconds = filteredTimeDiffs.stream()
                .mapToLong(OrderCallTimeDiff::getDiffSeconds)
                .sum();

        // 计算平均用时（秒）
        double averageSeconds = (double) totalSeconds / filteredTimeDiffs.size();

        // 转换为天（1天 = 86400秒）
        double averageDays = averageSeconds / 86400.0;

        // 返回结果，保留6位小数
        return BigDecimal.valueOf(averageDays).setScale(6, BigDecimal.ROUND_HALF_UP);
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
        // 获取所有订单与首电时间差数据
        List<OrderCallTimeDiff> allTimeDiffs = orderCallTimeDiffMapper.selectAllOrderCallTimeDiffs();

        // 过滤数据：去除时间为负数的记录和秒数大于432000的记录
        return allTimeDiffs.stream()
                .filter(diff -> diff.getDiffSeconds() != null)
                .filter(diff -> diff.getDiffSeconds() >= 0)           // 去除负数
                .filter(diff -> diff.getDiffSeconds() <= 432000)      // 去除大于5天的记录
                .collect(Collectors.toList());
    }

    /**
     * 获取统计数据
     *
     * @return 包含总记录数、符合条件记录数、平均用时（天）的统计信息
     */
    public TimeDiffStatistics getStatistics() {
        // 获取所有订单与首电时间差数据
        List<OrderCallTimeDiff> allTimeDiffs = orderCallTimeDiffMapper.selectAllOrderCallTimeDiffs();

        // 总记录数
        int totalCount = allTimeDiffs.size();

        // 过滤数据：去除时间为负数的记录和秒数大于432000的记录
        List<OrderCallTimeDiff> filteredTimeDiffs = allTimeDiffs.stream()
                .filter(diff -> diff.getDiffSeconds() != null)
                .filter(diff -> diff.getDiffSeconds() >= 0)           // 去除负数
                .filter(diff -> diff.getDiffSeconds() <= 432000)      // 去除大于5天的记录
                .collect(Collectors.toList());

        // 符合条件记录数
        int filteredCount = filteredTimeDiffs.size();

        // 计算平均用时（天）
        BigDecimal averageTimeInDays = BigDecimal.ZERO;
        if (filteredCount > 0) {
            // 计算符合条件的用时总和
            long totalSeconds = filteredTimeDiffs.stream()
                    .mapToLong(OrderCallTimeDiff::getDiffSeconds)
                    .sum();

            // 计算平均用时（秒）
            double averageSeconds = (double) totalSeconds / filteredCount;

            // 转换为天（1天 = 86400秒）
            double averageDays = averageSeconds / 86400.0;

            // 返回结果，保留6位小数
            averageTimeInDays = BigDecimal.valueOf(averageDays).setScale(6, BigDecimal.ROUND_HALF_UP);
        }

        return new TimeDiffStatistics(totalCount, filteredCount, averageTimeInDays);
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