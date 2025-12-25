package org.panjy.servicemetricsplatform.entity.message;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 微信账号实体类
 * 对应数据库表 wechat_account
 */
@Data
public class WechatAccount {
    
    /**
     * 主键ID，唯一标识一条微信账号记录
     */
    private Long id;
    
    /**
     * 微信ID（如 wxid_xxx）
     */
    private String wechatId;
    
    /**
     * 设备账号ID（关联设备表）
     */
    private Long deviceAccountId;
    
    /**
     * 设备IMEI号
     */
    private String imei;
    
    /**
     * 设备备注信息
     */
    private String deviceMemo;
    
    /**
     * 账号用户名（如F0746）
     */
    private String accountUserName;
    
    /**
     * 账号真实姓名
     */
    private String accountRealName;
    
    /**
     * 账号昵称
     */
    private String accountNickname;
    
    /**
     * 客服是否在线
     */
    private Boolean keFuAlive;
    
    /**
     * 设备是否在线
     */
    private Boolean deviceAlive;
    
    /**
     * 微信是否在线
     */
    private Boolean wechatAlive;
    
    /**
     * 昨日消息数量
     */
    private Integer yesterdayMsgCount;
    
    /**
     * 近7日消息数量
     */
    private Integer sevenDayMsgCount;
    
    /**
     * 近30日消息数量
     */
    private Integer thirtyDayMsgCount;
    
    /**
     * 好友总数
     */
    private Integer totalFriend;
    
    /**
     * 男性好友数
     */
    private Integer maleFriend;
    
    /**
     * 女性好友数
     */
    private Integer femaleFriend;
    
    /**
     * 微信群名称
     */
    private String wechatGroupName;
    
    /**
     * 租户ID
     */
    private Long tenantId;
    
    /**
     * 显示昵称
     */
    private String nickname;
    
    /**
     * 微信别名
     */
    private String alias;
    
    /**
     * 头像地址
     */
    private String avatar;
    
    /**
     * 性别（0=未知，1=男，2=女）
     */
    private Integer gender;
    
    /**
     * 地区
     */
    private String region;
    
    /**
     * 个性签名
     */
    private String signature;
    
    /**
     * 绑定QQ号
     */
    private String bindQq;
    
    /**
     * 绑定邮箱
     */
    private String bindEmail;
    
    /**
     * 绑定手机号
     */
    private String bindMobile;
    
    /**
     * 账号创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 当前绑定的设备ID
     */
    private Long currentDeviceId;
    
    /**
     * 是否已删除
     */
    private Boolean isDeleted;
    
    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
    
    /**
     * 分组ID
     */
    private Long groupId;
    
    /**
     * 备注
     */
    private String memo;
    
    /**
     * 微信版本号
     */
    private String wechatVersion;
    
    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;
}