package org.panjy.servicemetricsplatform.entity;

import java.math.BigDecimal;

/**
 * 个性化中医指导完成率实体类
 * 用于存储个性化中医指导完成率相关的统计信息
 */
public class PersonalizedGuidanceCompletionRate {

    /**
     * 超过1分钟(60秒)的通话次数
     */
    private Integer over1MinCount;

    /**
     * 超过15分钟(900秒)的通话次数
     */
    private Integer over15MinCount;

//    /**
//     * 个性化中医指导完成率
//     * 计算公式: over15MinCount / over1MinCount
//     */
//    private BigDecimal completionRate;

    // 默认构造函数
    public PersonalizedGuidanceCompletionRate() {
    }

    // 带参数的构造函数
    public PersonalizedGuidanceCompletionRate(Integer over1MinCount, Integer over15MinCount) {
        this.over1MinCount = over1MinCount;
        this.over15MinCount = over15MinCount;
//        this.completionRate = completionRate;
    }

    // Getter方法
    public Integer getOver1MinCount() {
        return over1MinCount;
    }

    public Integer getOver15MinCount() {
        return over15MinCount;
    }

//    public BigDecimal getCompletionRate() {
//        return completionRate;
//    }

    // Setter方法
    public void setOver1MinCount(Integer over1MinCount) {
        this.over1MinCount = over1MinCount;
    }

    public void setOver15MinCount(Integer over15MinCount) {
        this.over15MinCount = over15MinCount;
    }

//    public void setCompletionRate(BigDecimal completionRate) {
//        this.completionRate = completionRate;
//    }

    @Override
    public String toString() {
        return "PersonalizedGuidanceCompletionRate{" +
                "over1MinCount=" + over1MinCount +
                ", over15MinCount=" + over15MinCount +
                '}';
    }
}