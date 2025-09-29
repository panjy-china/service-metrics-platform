package org.panjy.servicemetricsplatform.entity;

import java.io.Serializable;

import jakarta.persistence.*;

/**
 * 微信会员实体类
 * 对应数据库表：tbl_wechat_member
 */
@Entity
@Table(name = "tbl_wechat_member")
public class WechatMember implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 微信ID
     */
    @Id
    @Column(name = "wechat_id")
    private String wechatId;
    
    /**
     * 客户ID
     */
    @Column(name = "colCltID")
    private String colCltID;
    
    // 默认构造函数
    public WechatMember() {
    }
    
    // 带参数的构造函数
    public WechatMember(String wechatId, String colCltID) {
        this.wechatId = wechatId;
        this.colCltID = colCltID;
    }
    
    // Getter方法
    public String getWechatId() {
        return wechatId;
    }
    
    public String getColCltID() {
        return colCltID;
    }
    
    // Setter方法
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    
    public void setColCltID(String colCltID) {
        this.colCltID = colCltID;
    }
    
    // 手动实现builder模式
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String wechatId;
        private String colCltID;
        
        public Builder wechatId(String wechatId) {
            this.wechatId = wechatId;
            return this;
        }
        
        public Builder colCltID(String colCltID) {
            this.colCltID = colCltID;
            return this;
        }
        
        public WechatMember build() {
            WechatMember obj = new WechatMember();
            obj.wechatId = this.wechatId;
            obj.colCltID = this.colCltID;
            return obj;
        }
    }
    
    @Override
    public String toString() {
        return "WechatMember{" +
                "wechatId='" + wechatId + '\'' +
                ", colCltID='" + colCltID + '\'' +
                '}';
    }
}