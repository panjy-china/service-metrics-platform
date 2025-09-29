package org.panjy.servicemetricsplatform.entity;

import java.time.LocalDate;

/**
 * UserMealCheckin实体类
 * 对应数据库表：aikang.user_meal_checkin
 */
public class UserMealCheckin {

    /**
     * 用户ID
     */
    private String wechatId;

    /**
     * 打卡日期
     */
    private LocalDate checkinDate;

    /**
     * 早餐是否打卡 (0=否, 1=是)
     */
    private Integer breakfastChecked;

    /**
     * 午餐是否打卡 (0=否, 1=是)
     */
    private Integer lunchChecked;

    /**
     * 晚餐是否打卡 (0=否, 1=是)
     */
    private Integer dinnerChecked;

    /**
     * 是否有体重反馈 (0=否, 1=是)
     */
    private Integer hasWeightFeedback;

    // 默认构造函数
    public UserMealCheckin() {
    }

    // 带参数的构造函数
    public UserMealCheckin(String wechatId, LocalDate checkinDate, 
                          Integer breakfastChecked, Integer lunchChecked, Integer dinnerChecked) {
        this.wechatId = wechatId;
        this.checkinDate = checkinDate;
        this.breakfastChecked = breakfastChecked;
        this.lunchChecked = lunchChecked;
        this.dinnerChecked = dinnerChecked;
    }

    // 带体重反馈的构造函数
    public UserMealCheckin(String wechatId, LocalDate checkinDate, 
                          Integer breakfastChecked, Integer lunchChecked, Integer dinnerChecked,
                          Integer hasWeightFeedback) {
        this.wechatId = wechatId;
        this.checkinDate = checkinDate;
        this.breakfastChecked = breakfastChecked;
        this.lunchChecked = lunchChecked;
        this.dinnerChecked = dinnerChecked;
        this.hasWeightFeedback = hasWeightFeedback;
    }

    // 全参数构造函数
    public UserMealCheckin(String wechatId, LocalDate checkinDate, 
                          int breakfastChecked, int lunchChecked, int dinnerChecked,
                          int hasWeightFeedback) {
        this.wechatId = wechatId;
        this.checkinDate = checkinDate;
        this.breakfastChecked = breakfastChecked;
        this.lunchChecked = lunchChecked;
        this.dinnerChecked = dinnerChecked;
        this.hasWeightFeedback = hasWeightFeedback;
    }

    // Getter方法
    public String getWechatId() {
        return wechatId;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public Integer getBreakfastChecked() {
        return breakfastChecked;
    }

    public Integer getLunchChecked() {
        return lunchChecked;
    }

    public Integer getDinnerChecked() {
        return dinnerChecked;
    }

    public Integer getHasWeightFeedback() {
        return hasWeightFeedback;
    }

    // Setter方法
    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        this.checkinDate = checkinDate;
    }

    public void setBreakfastChecked(Integer breakfastChecked) {
        this.breakfastChecked = breakfastChecked;
    }

    public void setLunchChecked(Integer lunchChecked) {
        this.lunchChecked = lunchChecked;
    }

    public void setDinnerChecked(Integer dinnerChecked) {
        this.dinnerChecked = dinnerChecked;
    }

    public void setHasWeightFeedback(Integer hasWeightFeedback) {
        this.hasWeightFeedback = hasWeightFeedback;
    }

    @Override
    public String toString() {
        return "UserMealCheckin{" +
                "wechatId='" + wechatId + '\'' +
                ", checkinDate=" + checkinDate +
                ", breakfastChecked=" + (breakfastChecked == 1 ? "是" : "否") +
                ", lunchChecked=" + (lunchChecked == 1 ? "是" : "否") +
                ", dinnerChecked=" + (dinnerChecked == 1 ? "是" : "否") +
                ", hasWeightFeedback=" + (hasWeightFeedback == 1 ? "是" : "否") +
                '}';
    }
}