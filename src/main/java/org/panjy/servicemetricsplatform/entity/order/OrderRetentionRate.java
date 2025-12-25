package org.panjy.servicemetricsplatform.entity.order;

/**
 * 推单三日留存率实体类
 */
public class OrderRetentionRate {
    
    /** 超过10天的用户数 */
    private Long over10DaysUsers;
    
    /** 超过13天的用户数 */
    private Long over13DaysUsers;
    
    /** 三日留存率 */
    private Double retentionRate;
    
    // 默认构造函数
    public OrderRetentionRate() {
    }
    
    // 带参数的构造函数
    public OrderRetentionRate(Long over10DaysUsers, Long over13DaysUsers, Double retentionRate) {
        this.over10DaysUsers = over10DaysUsers;
        this.over13DaysUsers = over13DaysUsers;
        this.retentionRate = retentionRate;
    }
    
    // Getter 和 Setter 方法
    public Long getOver10DaysUsers() {
        return over10DaysUsers;
    }
    
    public void setOver10DaysUsers(Long over10DaysUsers) {
        this.over10DaysUsers = over10DaysUsers;
    }
    
    public Long getOver13DaysUsers() {
        return over13DaysUsers;
    }
    
    public void setOver13DaysUsers(Long over13DaysUsers) {
        this.over13DaysUsers = over13DaysUsers;
    }
    
    public Double getRetentionRate() {
        return retentionRate;
    }
    
    public void setRetentionRate(Double retentionRate) {
        this.retentionRate = retentionRate;
    }
    
    // toString 方法
    @Override
    public String toString() {
        return "OrderRetentionRate{" +
                "over10DaysUsers=" + over10DaysUsers +
                ", over13DaysUsers=" + over13DaysUsers +
                ", retentionRate=" + retentionRate +
                '}';
    }
}