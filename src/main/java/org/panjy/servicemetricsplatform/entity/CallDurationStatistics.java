package org.panjy.servicemetricsplatform.entity;

/**
 * 通话时长统计实体类
 */
public class CallDurationStatistics {
    
    /**
     * 时长区间
     */
    private String durationRange;
    
    /**
     * 记录数量
     */
    private Long recordCount;
    
    // 默认构造函数
    public CallDurationStatistics() {
    }
    
    // 带参数的构造函数
    public CallDurationStatistics(String durationRange, Long recordCount) {
        this.durationRange = durationRange;
        this.recordCount = recordCount;
    }
    
    // Getter方法
    public String getDurationRange() {
        return durationRange;
    }
    
    public Long getRecordCount() {
        return recordCount;
    }
    
    // Setter方法
    public void setDurationRange(String durationRange) {
        this.durationRange = durationRange;
    }
    
    public void setRecordCount(Long recordCount) {
        this.recordCount = recordCount;
    }
    
    @Override
    public String toString() {
        return "CallDurationStatistics{" +
                "durationRange='" + durationRange + '\'' +
                ", recordCount=" + recordCount +
                '}';
    }
}