package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.mealcomletion.UserMealCheckin;

import java.time.LocalDateTime;
import java.util.List;

/**
 * UserMealCheckin数据访问接口
 * 提供对aikang.user_meal_checkin表的批量查询与批量插入操作
 */
@Mapper
public interface UserMealCheckinMapper {
    
    /**
     * 批量查询用户餐食打卡记录
     * 
     * @param userMealCheckins 用户餐食打卡记录列表
     * @return 查询结果列表
     */
    List<UserMealCheckin> batchSelect(@Param("userMealCheckins") List<UserMealCheckin> userMealCheckins);
    
    /**
     * 批量插入用户餐食打卡记录
     * 
     * @param userMealCheckins 用户餐食打卡记录列表
     * @return 受影响的行数
     */
    int batchInsert(@Param("userMealCheckins") List<UserMealCheckin> userMealCheckins);
    
    /**
     * 计算总打卡次数（所有记录的早中晚打卡次数之和）
     * 
     * @return 总打卡次数
     */
    int calculateTotalCheckinCount();
    
    /**
     * 计算指定微信用户的总服务天数
     * 
     * @param wechatId 微信ID
     * @return 总服务天数
     */
    int calculateTotalServiceDaysByWechatId(@Param("wechatId") String wechatId);
    
    /**
     * 计算指定微信用户的总打卡次数
     * 
     * @param wechatId 微信ID
     * @return 用户总打卡次数
     */
    int calculateUserTotalCheckinCount(@Param("wechatId") String wechatId);
    
    /**
     * 计算有体重反馈的记录数
     * 
     * @return 有体重反馈的记录数
     */
    int calculateWeightFeedbackCount();
    
    /**
     * 计算总记录数
     * 
     * @return 总记录数
     */
    int calculateTotalRecords();
    
    /**
     * 计算指定月份的总打卡次数（所有记录的早中晚打卡次数之和）
     * 
     * @param targetDate 目标日期
     * @return 指定月份的总打卡次数
     */
    int calculateTotalCheckinCountByMonth(@Param("targetDate") LocalDateTime targetDate);
    
    /**
     * 计算指定月份有体重反馈的记录数
     * 
     * @param targetDate 目标日期
     * @return 指定月份有体重反馈的记录数
     */
    int calculateWeightFeedbackCountByMonth(@Param("targetDate") LocalDateTime targetDate);
    
    /**
     * 计算指定月份的总记录数
     * 
     * @param targetDate 目标日期
     * @return 指定月份的总记录数
     */
    int calculateTotalRecordsByMonth(@Param("targetDate") LocalDateTime targetDate);
}