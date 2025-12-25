package org.panjy.servicemetricsplatform.entity.call;

/**
 * 通话次数统计实体类
 * 对应数据库表：aikang.user_call_count
 */
public class CallCountSummary {

    /**
     * 客户ID
     */
    private String colCltID;

    /**
     * 通话次数
     */
    private Integer callCount;

    // 默认构造函数
    public CallCountSummary() {
    }

    // 带参数的构造函数
    public CallCountSummary(String colCltID, Integer callCount) {
        this.colCltID = colCltID;
        this.callCount = callCount;
    }

    // Getter方法
    public String getColCltID() {
        return colCltID;
    }

    public Integer getCallCount() {
        return callCount;
    }

    // Setter方法
    public void setColCltID(String colCltID) {
        this.colCltID = colCltID;
    }

    public void setCallCount(Integer callCount) {
        this.callCount = callCount;
    }

    @Override
    public String toString() {
        return "CallCountSummary{" +
                "colCltID='" + colCltID + '\'' +
                ", callCount=" + callCount +
                '}';
    }
}