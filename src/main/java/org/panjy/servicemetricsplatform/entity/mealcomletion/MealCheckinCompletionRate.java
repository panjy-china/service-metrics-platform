package org.panjy.servicemetricsplatform.entity.mealcomletion;

/**
 * 餐食打卡完成率实体类
 */
public class MealCheckinCompletionRate {
    
    /**
     * 时间段名称
     */
    private String rangeName;
    
    /**
     * 完成率
     */
    private Double completionRate;
    
    // 默认构造函数
    public MealCheckinCompletionRate() {
    }
    
    // 带参数的构造函数
    public MealCheckinCompletionRate(String rangeName, Double completionRate) {
        this.rangeName = rangeName;
        this.completionRate = completionRate;
    }
    
    // Getter方法
    public String getRangeName() {
        return rangeName;
    }
    
    public Double getCompletionRate() {
        return completionRate;
    }
    
    // Setter方法
    public void setRangeName(String rangeName) {
        this.rangeName = rangeName;
    }
    
    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }
    
    @Override
    public String toString() {
        return "MealCheckinCompletionRate{" +
                "rangeName='" + rangeName + '\'' +
                ", completionRate=" + completionRate +
                '}';
    }
}