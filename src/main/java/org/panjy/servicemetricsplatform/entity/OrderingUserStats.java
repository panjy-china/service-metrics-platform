package org.panjy.servicemetricsplatform.entity;

import java.time.LocalDate;

/**
 * 下单用户统计实体类
 * 用于封装日、周、月下单用户数统计结果
 */
public class OrderingUserStats {
    
    /** 统计日期 */
    private LocalDate date;
    
    /** 日下单用户数 */
    private Long dailyOrderingUsers;
    
    /** 周下单用户数 */
    private Long weeklyOrderingUsers;
    
    /** 月下单用户数 */
    private Long monthlyOrderingUsers;
    
    // 无参构造函数
    public OrderingUserStats() {
    }
    
    // 有参构造函数
    public OrderingUserStats(LocalDate date, Long dailyOrderingUsers, Long weeklyOrderingUsers, Long monthlyOrderingUsers) {
        this.date = date;
        this.dailyOrderingUsers = dailyOrderingUsers;
        this.weeklyOrderingUsers = weeklyOrderingUsers;
        this.monthlyOrderingUsers = monthlyOrderingUsers;
    }
    
    // Getter和Setter方法
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public Long getDailyOrderingUsers() {
        return dailyOrderingUsers;
    }
    
    public void setDailyOrderingUsers(Long dailyOrderingUsers) {
        this.dailyOrderingUsers = dailyOrderingUsers;
    }
    
    public Long getWeeklyOrderingUsers() {
        return weeklyOrderingUsers;
    }
    
    public void setWeeklyOrderingUsers(Long weeklyOrderingUsers) {
        this.weeklyOrderingUsers = weeklyOrderingUsers;
    }
    
    public Long getMonthlyOrderingUsers() {
        return monthlyOrderingUsers;
    }
    
    public void setMonthlyOrderingUsers(Long monthlyOrderingUsers) {
        this.monthlyOrderingUsers = monthlyOrderingUsers;
    }
    
    @Override
    public String toString() {
        return "OrderingUserStats{" +
                "date=" + date +
                ", dailyOrderingUsers=" + dailyOrderingUsers +
                ", weeklyOrderingUsers=" + weeklyOrderingUsers +
                ", monthlyOrderingUsers=" + monthlyOrderingUsers +
                '}';
    }
}