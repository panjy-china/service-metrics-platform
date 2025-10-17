package org.panjy.servicemetricsplatform.entity;

import java.math.BigDecimal;

/**
 * 销售排行榜实体类
 * 用于存储销售额排行榜数据
 */
public class SalesRanking {
    
    /** 销售员代码 */
    private String salesCode;
    
    /** 销售员姓名 */
    private String salesName;
    
    /** 总销售额 */
    private BigDecimal totalSales;
    
    // 无参构造函数
    public SalesRanking() {
    }
    
    // 有参构造函数
    public SalesRanking(String salesCode, String salesName, BigDecimal totalSales) {
        this.salesCode = salesCode;
        this.salesName = salesName;
        this.totalSales = totalSales;
    }
    
    // Getter和Setter方法
    public String getSalesCode() {
        return salesCode;
    }
    
    public void setSalesCode(String salesCode) {
        this.salesCode = salesCode;
    }
    
    public String getSalesName() {
        return salesName;
    }
    
    public void setSalesName(String salesName) {
        this.salesName = salesName;
    }
    
    public BigDecimal getTotalSales() {
        return totalSales;
    }
    
    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }
    
    @Override
    public String toString() {
        return "SalesRanking{" +
                "salesCode='" + salesCode + '\'' +
                ", salesName='" + salesName + '\'' +
                ", totalSales=" + totalSales +
                '}';
    }
}