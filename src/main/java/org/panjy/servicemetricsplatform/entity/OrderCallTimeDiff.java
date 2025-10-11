package org.panjy.servicemetricsplatform.entity;

import java.time.LocalDateTime;

/**
 * 订单与首电时间差实体类
 * 用于存储订单时间与首电时间的差值信息
 */
public class OrderCallTimeDiff {

    /**
     * 客户ID
     */
    private String colCltID;

    /**
     * 首次订单时间
     */
    private LocalDateTime firstOrderTime;

    /**
     * 首电日期
     */
    private LocalDateTime firstCallDate;

    /**
     * 时间差（秒）
     */
    private Long diffSeconds;

    // 默认构造函数
    public OrderCallTimeDiff() {
    }

    // 带参数的构造函数
    public OrderCallTimeDiff(String colCltID, LocalDateTime firstOrderTime, LocalDateTime firstCallDate, Long diffSeconds) {
        this.colCltID = colCltID;
        this.firstOrderTime = firstOrderTime;
        this.firstCallDate = firstCallDate;
        this.diffSeconds = diffSeconds;
    }

    // Getter方法
    public String getColCltID() {
        return colCltID;
    }

    public LocalDateTime getFirstOrderTime() {
        return firstOrderTime;
    }

    public LocalDateTime getFirstCallDate() {
        return firstCallDate;
    }

    public Long getDiffSeconds() {
        return diffSeconds;
    }

    // Setter方法
    public void setColCltID(String colCltID) {
        this.colCltID = colCltID;
    }

    public void setFirstOrderTime(LocalDateTime firstOrderTime) {
        this.firstOrderTime = firstOrderTime;
    }

    public void setFirstCallDate(LocalDateTime firstCallDate) {
        this.firstCallDate = firstCallDate;
    }

    public void setDiffSeconds(Long diffSeconds) {
        this.diffSeconds = diffSeconds;
    }

    @Override
    public String toString() {
        return "OrderCallTimeDiff{" +
                "colCltID='" + colCltID + '\'' +
                ", firstOrderTime=" + firstOrderTime +
                ", firstCallDate=" + firstCallDate +
                ", diffSeconds=" + diffSeconds +
                '}';
    }
}