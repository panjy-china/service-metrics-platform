package org.panjy.servicemetricsplatform.entity;

/**
 * 指导次数汇总实体类
 */
public class GuidanceSummary {
    
    /**
     * 总指导次数
     */
    private Integer totalGuidanceCount;
    
    /**
     * 总个性化指导次数
     */
    private Integer totalPersonalizedGuidanceCount;
    
    // 默认构造函数
    public GuidanceSummary() {
    }
    
    // 带参数的构造函数
    public GuidanceSummary(Integer totalGuidanceCount, Integer totalPersonalizedGuidanceCount) {
        this.totalGuidanceCount = totalGuidanceCount;
        this.totalPersonalizedGuidanceCount = totalPersonalizedGuidanceCount;
    }
    
    // Getter方法
    public Integer getTotalGuidanceCount() {
        return totalGuidanceCount;
    }
    
    public Integer getTotalPersonalizedGuidanceCount() {
        return totalPersonalizedGuidanceCount;
    }
    
    // Setter方法
    public void setTotalGuidanceCount(Integer totalGuidanceCount) {
        this.totalGuidanceCount = totalGuidanceCount;
    }
    
    public void setTotalPersonalizedGuidanceCount(Integer totalPersonalizedGuidanceCount) {
        this.totalPersonalizedGuidanceCount = totalPersonalizedGuidanceCount;
    }
    
    @Override
    public String toString() {
        return "GuidanceSummary{" +
                "totalGuidanceCount=" + totalGuidanceCount +
                ", totalPersonalizedGuidanceCount=" + totalPersonalizedGuidanceCount +
                '}';
    }
}