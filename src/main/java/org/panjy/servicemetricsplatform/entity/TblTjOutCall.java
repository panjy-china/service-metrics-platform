package org.panjy.servicemetricsplatform.entity;

import java.time.LocalDateTime;

/**
 * Tbl_Tj_OutCall实体类
 * 对应数据库表：aikang.Tbl_Tj_OutCall
 */
public class TblTjOutCall {

    /**
     * 客户ID
     */
    private String colCltID;

    /**
     * DNISANS字段
     */
    private Integer DNISANS;

    /**
     * DNANSTIME字段
     */
    private LocalDateTime DNANSTIME;

    /**
     * DNOUTTIME字段
     */
    private LocalDateTime DNOUTTIME;

    /**
     * DNINTIME字段
     */
    private LocalDateTime DNINTIME;

    // 默认构造函数
    public TblTjOutCall() {
    }

    // 带参数的构造函数
    public TblTjOutCall(String colCltID, Integer DNISANS, LocalDateTime DNANSTIME, 
                        LocalDateTime DNOUTTIME, LocalDateTime DNINTIME) {
        this.colCltID = colCltID;
        this.DNISANS = DNISANS;
        this.DNANSTIME = DNANSTIME;
        this.DNOUTTIME = DNOUTTIME;
        this.DNINTIME = DNINTIME;
    }

    // Getter方法
    public String getColCltID() {
        return colCltID;
    }

    public Integer getDNISANS() {
        return DNISANS;
    }

    public LocalDateTime getDNANSTIME() {
        return DNANSTIME;
    }

    public LocalDateTime getDNOUTTIME() {
        return DNOUTTIME;
    }

    public LocalDateTime getDNINTIME() {
        return DNINTIME;
    }

    // Setter方法
    public void setColCltID(String colCltID) {
        this.colCltID = colCltID;
    }

    public void setDNISANS(Integer DNISANS) {
        this.DNISANS = DNISANS;
    }

    public void setDNANSTIME(LocalDateTime DNANSTIME) {
        this.DNANSTIME = DNANSTIME;
    }

    public void setDNOUTTIME(LocalDateTime DNOUTTIME) {
        this.DNOUTTIME = DNOUTTIME;
    }

    public void setDNINTIME(LocalDateTime DNINTIME) {
        this.DNINTIME = DNINTIME;
    }

    @Override
    public String toString() {
        return "TblTjOutCall{" +
                "colCltID='" + colCltID + '\'' +
                ", DNISANS=" + DNISANS +
                ", DNANSTIME=" + DNANSTIME +
                ", DNOUTTIME=" + DNOUTTIME +
                ", DNINTIME=" + DNINTIME +
                '}';
    }
}