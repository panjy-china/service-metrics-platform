package org.panjy.servicemetricsplatform.entity.message;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * (WechatGroupMessage)实体类
 *
 * @author makejava
 * @since 2025-08-28 17:45:43
 */
@Data
@Entity
@Table(name = "wechat_group_message")
@AllArgsConstructor
@NoArgsConstructor
public class WechatGroupMessage implements Serializable {
    private static final long serialVersionUID = 509025256726593593L;
/**
     * 消息记录ID
     */
    @Id
    private Long id;
/**
     * 群ID（关联 wechat_group.id）
     */
    private Long wechatChatroomId;
/**
     * 发送者微信ID（可为空）
     */
    private String sender;
/**
     * 关联微信账号ID
     */
    private Long wechatAccountId;
/**
     * 微信账号
     */
    private String wechatId;
/**
     * 租户ID
     */
    private Long tenantId;
/**
     * 账号ID
     */
    private Long accountId;
/**
     * 协作账号ID
     */
    private Long synergyAccountId;
/**
     * 消息内容（可能为JSON字符串）
     */
    private String content;
/**
     * 消息类型
     */
    private Long msgType;
/**
     * 消息子类型
     */
    private Long msgSubType;
/**
     * 微信服务器消息ID
     */
    private String msgSvrId;
/**
     * 是否为自己发送
     */
    private Integer isSend;
/**
     * 创建时间
     */
    private Date createTime;
/**
     * 是否已删除
     */
    private Integer isDeleted;
/**
     * 删除时间
     */
    private Date deleteTime;
/**
     * 发送状态
     */
    private Integer sendStatus;
/**
     * 微信消息时间戳
     */
    private Long wechatTime;
/**
     * 来源
     */
    private Integer origin;
/**
     * 消息内部ID
     */
    private Long msgId;
/**
     * 是否撤回
     */
    private Integer recalled;
}

