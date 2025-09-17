package org.panjy.servicemetricsplatform.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.io.Serializable;

/**
 * (WechatMember)实体类
 *
 * @author makejava
 * @since 2025-08-28 17:37:06
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wechat_member")
public class WechatMember implements Serializable {
    private static final long serialVersionUID = 352891375372870054L;
/**
     * 主键ID
     */
    @Id
    private Long id;
/**
     * 成员微信ID
     */
    private String wechatId;
/**
     * 成员昵称
     */
    private String nickname;
/**
     * 成员头像
     */
    private String avatar;
/**
     * 是否管理员
     */
    private Integer isAdmin;
/**
     * 是否已删除
     */
    private Integer isDeleted;
/**
     * 删除时间
     */
    private Date deletedDate;
}

