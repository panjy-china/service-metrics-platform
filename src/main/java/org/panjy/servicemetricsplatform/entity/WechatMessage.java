package org.panjy.servicemetricsplatform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Date;
import java.io.Serializable;

/**
 * 聊天记录信息表(WechatMessage)实体类
 *
 * @author makejava
 * @since 2025-08-28 14:49:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "wechat_message")
public class WechatMessage implements Serializable {
    private static final long serialVersionUID = -50592899931702822L;

    @Id
    private Long id;
/**
     * 微信昵称
     */

    private String wechatNickname;
/**
     * 微信ID
     */
    private String wechatId;
/**
     * 微信账户
     */
    private String wechatAccount;
/**
     * 好友昵称
     */
    private String friendNickname;
/**
     * 好友微信ID
     */
    private String friendWechatId;
/**
     * 好友微信账户
     */
    private String friendWechatAccount;
/**
     * 好友备注
     */
    private String friendRemark;
/**
     * 好友分类
     */
    private String friendCategory;
/**
     * 消息内容
     */
    private String content;
/**
     * 发送者
     */
    private String sender;
/**
     * 消息类型
     */
    private String messageType;
/**
     * 发送状态
     */
    private String sendStatus;
/**
     * 聊天时间
     */
    private Date chatTime;
/**
     * 创建时间
     */
    private Date createdTime;
/**
     * 更新时间
     */
    private Date updatedTime;
}

