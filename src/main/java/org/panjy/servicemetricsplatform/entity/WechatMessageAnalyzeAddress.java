package org.panjy.servicemetricsplatform.entity;

import java.io.Serializable;

/**
 * 微信消息地址分析结果实体类
 * 对应数据库表：wechat_message_a_analyze_address
 */
public class WechatMessageAnalyzeAddress implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 客户微信号
     */
    private String wechatId;
    
    /**
     * 消息类型（例如：文本、图片等）
     */
    private Integer msgType;
    
    /**
     * 微信服务器时间戳（毫秒）
     */
    private Long wechatTime;
    
    /**
     * 聊天消息的内容
     */
    private String content;
    
    /**
     * AI分析后的地址
     */
    private String address;
    
    // 默认构造函数
    public WechatMessageAnalyzeAddress() {
    }
    
    // 带参数的构造函数
    public WechatMessageAnalyzeAddress(String wechatId, Integer msgType, Long wechatTime, String content, String address) {
        this.wechatId = wechatId;
        this.msgType = msgType;
        this.wechatTime = wechatTime;
        this.content = content;
        this.address = address;
    }
    
    // Getter方法
    public String getWechatId() {
        return wechatId;
    }
    
    public Integer getMsgType() {
        return msgType;
    }
    
    public Long getWechatTime() {
        return wechatTime;
    }
    
    public String getContent() {
        return content;
    }
    
    public String getAddress() {
        return address;
    }
    
    // Setter方法
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }
    
    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
    
    public void setWechatTime(Long wechatTime) {
        this.wechatTime = wechatTime;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    // 手动实现builder模式
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String wechatId;
        private Integer msgType;
        private Long wechatTime;
        private String content;
        private String address;
        
        public Builder wechatId(String wechatId) {
            this.wechatId = wechatId;
            return this;
        }
        
        public Builder msgType(Integer msgType) {
            this.msgType = msgType;
            return this;
        }
        
        public Builder wechatTime(Long wechatTime) {
            this.wechatTime = wechatTime;
            return this;
        }
        
        public Builder content(String content) {
            this.content = content;
            return this;
        }
        
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        
        public WechatMessageAnalyzeAddress build() {
            WechatMessageAnalyzeAddress obj = new WechatMessageAnalyzeAddress();
            obj.wechatId = this.wechatId;
            obj.msgType = this.msgType;
            obj.wechatTime = this.wechatTime;
            obj.content = this.content;
            obj.address = this.address;
            return obj;
        }
    }
    
    @Override
    public String toString() {
        return "WechatMessageAnalyzeAddress{" +
                "wechatId='" + wechatId + '\'' +
                ", msgType=" + msgType +
                ", wechatTime=" + wechatTime +
                ", content='" + content + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}