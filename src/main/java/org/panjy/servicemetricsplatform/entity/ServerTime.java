package org.panjy.servicemetricsplatform.entity;

import java.time.LocalDateTime;

/**
 * 服务时间实体类
 * 对应ClickHouse中的aikang.tbl_ServerTime表
 */
public class ServerTime {
    
    /** 客户ID */
    private String colCltID;
    
    /** 服务时长（秒） */
    private Long colSerTi;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    // 默认构造函数
    public ServerTime() {
    }
    
    // 带参数的构造函数
    public ServerTime(String colCltID, Long colSerTi, LocalDateTime createTime, LocalDateTime updateTime) {
        this.colCltID = colCltID;
        this.colSerTi = colSerTi;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    
    // Getter 和 Setter 方法
    public String getColCltID() {
        return colCltID;
    }
    
    public void setColCltID(String colCltID) {
        this.colCltID = colCltID;
    }
    
    public Long getColSerTi() {
        return colSerTi;
    }
    
    public void setColSerTi(Long colSerTi) {
        this.colSerTi = colSerTi;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    // toString 方法
    @Override
    public String toString() {
        return "ServerTime{" +
                "colCltID='" + colCltID + '\'' +
                ", colSerTi=" + colSerTi +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}