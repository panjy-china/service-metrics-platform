package org.panjy.servicemetricsplatform.entity;

/**
 * 通话统计实体类
 * 用于存储通话总次数、长通话次数和个性化指导率
 */
public class CallStatistics {

    /**
     * 微信ID
     */
    private String wechatId;

    /**
     * 通话总次数
     */
    private Integer totalCalls;

    /**
     * 长通话次数（>= 900秒）
     */
    private Integer longCalls;

    /**
     * 个性化指导率（长通话次数/总通话次数）
     */
    private Double personalizedGuidanceRate;

    // 默认构造函数
    public CallStatistics() {
    }

    // 带参数的构造函数
    public CallStatistics(String wechatId, Integer totalCalls, Integer longCalls, Double personalizedGuidanceRate) {
        this.wechatId = wechatId;
        this.totalCalls = totalCalls;
        this.longCalls = longCalls;
        this.personalizedGuidanceRate = personalizedGuidanceRate;
    }

    // Getter方法
    public String getWechatId() {
        return wechatId;
    }

    public Integer getTotalCalls() {
        return totalCalls;
    }

    public Integer getLongCalls() {
        return longCalls;
    }

    public Double getPersonalizedGuidanceRate() {
        return personalizedGuidanceRate;
    }

    // Setter方法
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public void setTotalCalls(Integer totalCalls) {
        this.totalCalls = totalCalls;
    }

    public void setLongCalls(Integer longCalls) {
        this.longCalls = longCalls;
    }

    public void setPersonalizedGuidanceRate(Double personalizedGuidanceRate) {
        this.personalizedGuidanceRate = personalizedGuidanceRate;
    }

    @Override
    public String toString() {
        return "CallStatistics{" +
                "wechatId='" + wechatId + '\'' +
                ", totalCalls=" + totalCalls +
                ", longCalls=" + longCalls +
                ", personalizedGuidanceRate=" + personalizedGuidanceRate +
                '}';
    }
}