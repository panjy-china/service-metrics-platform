package org.panjy.servicemetricsplatform.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.io.Serializable;

/**
 * 微信消息地址分析结果实体类
 * 对应数据库表：wechat_message_a_analyze_address
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}