package org.panjy.servicemetricsplatform.entity.call;

import java.time.LocalDateTime;

/**
 * 首通电话摘要实体类
 * 对应数据库表：aikang.tbl_first_call_summary
 */
public class FirstCallSummary {

    /**
     * 客户ID
     */
    private String colCltID;

    /**
     * 首通日期（最早接听并挂断的通话）
     */
    private LocalDateTime firstCallDate;

    /**
     * 通话时长（秒）
     */
    private Long callDuration;

    // 默认构造函数
    public FirstCallSummary() {
    }

    // 带参数的构造函数
    public FirstCallSummary(String colCltID, LocalDateTime firstCallDate, Long callDuration) {
        this.colCltID = colCltID;
        this.firstCallDate = firstCallDate;
        this.callDuration = callDuration;
    }

    // Getter方法
    public String getColCltID() {
        return colCltID;
    }

    public LocalDateTime getFirstCallDate() {
        return firstCallDate;
    }

    public Long getCallDuration() {
        return callDuration;
    }

    // Setter方法
    public void setColCltID(String colCltID) {
        this.colCltID = colCltID;
    }

    public void setFirstCallDate(LocalDateTime firstCallDate) {
        this.firstCallDate = firstCallDate;
    }

    public void setCallDuration(Long callDuration) {
        this.callDuration = callDuration;
    }

    @Override
    public String toString() {
        return "FirstCallSummary{" +
                "colCltID='" + colCltID + '\'' +
                ", firstCallDate=" + firstCallDate +
                ", callDuration=" + callDuration +
                '}';
    }
}