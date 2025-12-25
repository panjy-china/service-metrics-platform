package org.panjy.servicemetricsplatform.entity.label;

import java.time.LocalDateTime;

/**
 * 微信用户健康画像标签实体类
 * 对应数据库表: aikang.wechat_user_label
 */
public class WechatUserLabel {

    /**
     * 微信好友（客户）ID，作为核心标识
     */
    private String wechatFriendId;

    /**
     * 微信销售账号ID
     */
    private String wechatAccountId;

    /**
     * 健康画像标签，如"高血压"、"双膝关节疼"等
     */
    private String label;

    /**
     * 标签提取依据，通常为客户原始聊天内容或摘要
     */
    private String evidence;

    /**
     * 标签首次创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 标签最后更新时间
     */
    private LocalDateTime updatedAt;

    // Constructors
    public WechatUserLabel() {}

    public WechatUserLabel(String wechatFriendId, String wechatAccountId, String label, String evidence, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.wechatFriendId = wechatFriendId;
        this.wechatAccountId = wechatAccountId;
        this.label = label;
        this.evidence = evidence;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public String getWechatFriendId() {
        return wechatFriendId;
    }

    public void setWechatFriendId(String wechatFriendId) {
        this.wechatFriendId = wechatFriendId;
    }

    public String getWechatAccountId() {
        return wechatAccountId;
    }

    public void setWechatAccountId(String wechatAccountId) {
        this.wechatAccountId = wechatAccountId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "WechatUserLabel{" +
                "wechatFriendId='" + wechatFriendId + '\'' +
                ", wechatAccountId='" + wechatAccountId + '\'' +
                ", label='" + label + '\'' +
                ", evidence='" + evidence + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}