package org.panjy.servicemetricsplatform.entity.order;

/**
 * 订单统计实体类
 * 用于保存日、周、月订单数量统计
 */
public class OrderStatistics {
    private Integer dailyCount;   // 日订单数
    private Integer weeklyCount;  // 周订单数
    private Integer monthlyCount; // 月订单数

    public OrderStatistics() {}

    public OrderStatistics(Integer dailyCount, Integer weeklyCount, Integer monthlyCount) {
        this.dailyCount = dailyCount;
        this.weeklyCount = weeklyCount;
        this.monthlyCount = monthlyCount;
    }

    // Getter方法
    public Integer getDailyCount() {
        return dailyCount;
    }

    public Integer getWeeklyCount() {
        return weeklyCount;
    }

    public Integer getMonthlyCount() {
        return monthlyCount;
    }

    // Setter方法
    public void setDailyCount(Integer dailyCount) {
        this.dailyCount = dailyCount;
    }

    public void setWeeklyCount(Integer weeklyCount) {
        this.weeklyCount = weeklyCount;
    }

    public void setMonthlyCount(Integer monthlyCount) {
        this.monthlyCount = monthlyCount;
    }

    @Override
    public String toString() {
        return "OrderStatistics{" +
                "dailyCount=" + dailyCount +
                ", weeklyCount=" + weeklyCount +
                ", monthlyCount=" + monthlyCount +
                '}';
    }
}