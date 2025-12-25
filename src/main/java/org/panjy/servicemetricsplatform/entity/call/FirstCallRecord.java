package org.panjy.servicemetricsplatform.entity.call;

import java.time.LocalDateTime;

/**
 * 首通电话记录实体类
 * 包含微信ID和首通电话摘要信息
 */
public class FirstCallRecord {

    /**
     * 微信ID
     */
    private String wechatId;

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
    public FirstCallRecord() {
    }

    // 带参数的构造函数
    public FirstCallRecord(String wechatId, String colCltID, LocalDateTime firstCallDate, Long callDuration) {
        this.wechatId = wechatId;
        this.colCltID = colCltID;
        this.firstCallDate = firstCallDate;
        this.callDuration = callDuration;
    }

    // Getter方法
    public String getWechatId() {
        return wechatId;
    }

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
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

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
        return "FirstCallRecord{" +
                "wechatId='" + wechatId + '\'' +
                ", colCltID='" + colCltID + '\'' +
                ", firstCallDate=" + firstCallDate +
                ", callDuration=" + callDuration +
                '}';
    }
}