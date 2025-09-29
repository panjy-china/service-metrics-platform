package org.panjy.servicemetricsplatform.entity;

import java.time.LocalDate;

/**
 * UserGuidanceStat实体类
 * 对应数据库表：aikang.user_guidance_stat
 */
public class UserGuidanceStat {

    /**
     * 统计日期
     */
    private LocalDate createTime;

    /**
     * 用户微信ID
     */
    private String wechatId;

    /**
     * 指导次数
     */
    private Integer guidanceCount;

    /**
     * 个性化指导次数
     */
    private Integer personalizedGuidanceCount;

    // 默认构造函数
    public UserGuidanceStat() {
    }

    // 带参数的构造函数
    public UserGuidanceStat(LocalDate createTime, String wechatId, Integer guidanceCount, Integer personalizedGuidanceCount) {
        this.createTime = createTime;
        this.wechatId = wechatId;
        this.guidanceCount = guidanceCount;
        this.personalizedGuidanceCount = personalizedGuidanceCount;
    }

    // Getter方法
    public LocalDate getCreateTime() {
        return createTime;
    }

    public String getWechatId() {
        return wechatId;
    }

    public Integer getGuidanceCount() {
        return guidanceCount;
    }

    public Integer getPersonalizedGuidanceCount() {
        return personalizedGuidanceCount;
    }

    // Setter方法
    public void setCreateTime(LocalDate createTime) {
        this.createTime = createTime;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public void setGuidanceCount(Integer guidanceCount) {
        this.guidanceCount = guidanceCount;
    }

    public void setPersonalizedGuidanceCount(Integer personalizedGuidanceCount) {
        this.personalizedGuidanceCount = personalizedGuidanceCount;
    }

    @Override
    public String toString() {
        return "UserGuidanceStat{" +
                "createTime=" + createTime +
                ", wechatId='" + wechatId + '\'' +
                ", guidanceCount=" + guidanceCount +
                ", personalizedGuidanceCount=" + personalizedGuidanceCount +
                '}';
    }
}