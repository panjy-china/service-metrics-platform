package org.panjy.servicemetricsplatform.service;

import org.panjy.servicemetricsplatform.entity.UserMealCheckin;
import org.panjy.servicemetricsplatform.mapper.UserMealCheckinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserMealCheckin服务类
 * 提供对用户餐食打卡记录的业务逻辑处理
 */
@Service
public class UserMealCheckinService {
    
    @Autowired
    private UserMealCheckinMapper userMealCheckinMapper;
    
    /**
     * 批量查询用户餐食打卡记录
     * 
     * @param userMealCheckins 查询条件列表
     * @return 查询结果列表
     */
    public List<UserMealCheckin> batchSelect(List<UserMealCheckin> userMealCheckins) {
        return userMealCheckinMapper.batchSelect(userMealCheckins);
    }
    
    /**
     * 批量插入用户餐食打卡记录
     * 
     * @param userMealCheckins 用户餐食打卡记录列表
     * @return 受影响的行数
     */
    public int batchInsert(List<UserMealCheckin> userMealCheckins) {
        return userMealCheckinMapper.batchInsert(userMealCheckins);
    }
    
    /**
     * 计算总打卡次数（所有记录的早中晚打卡次数之和）
     * 
     * @return 总打卡次数
     */
    public int calculateTotalCheckinCount() {
        return userMealCheckinMapper.calculateTotalCheckinCount();
    }
    
    /**
     * 计算指定微信用户的总服务天数
     * 
     * @param wechatId 微信ID
     * @return 总服务天数
     */
    public int calculateTotalServiceDaysByWechatId(String wechatId) {
        return userMealCheckinMapper.calculateTotalServiceDaysByWechatId(wechatId);
    }
    
    /**
     * 计算指定微信用户的总打卡次数
     * 
     * @param wechatId 微信ID
     * @return 用户总打卡次数
     */
    public int calculateUserTotalCheckinCount(String wechatId) {
        return userMealCheckinMapper.calculateUserTotalCheckinCount(wechatId);
    }
    
    /**
     * 计算所有用户三餐打卡率
     * 
     * @return 所有用户三餐打卡率，格式为百分比字符串
     */
    public String calculateAllUsersMealCheckinRate() {
        int totalCheckinCount = userMealCheckinMapper.calculateTotalCheckinCount();
        int totalRecords = userMealCheckinMapper.calculateTotalRecords();
        
        if (totalRecords == 0) {
            return "0.00%";
        }
        
        double rate = (double) totalCheckinCount / (totalRecords * 3) * 100;
        return String.format("%.2f%%", rate);
    }
    
    /**
     * 计算体重反馈完成率
     * 
     * @return 体重反馈完成率，格式为百分比字符串
     */
    public String calculateWeightFeedbackCompletionRate() {
        int weightFeedbackCount = userMealCheckinMapper.calculateWeightFeedbackCount();
        int totalRecords = userMealCheckinMapper.calculateTotalRecords();
        
        if (totalRecords == 0) {
            return "0.00%";
        }
        
        double rate = (double) weightFeedbackCount / totalRecords * 100;
        return String.format("%.2f%%", rate);
    }
    
    /**
     * 获取体重反馈统计信息
     * 
     * @return 包含有体重反馈记录数和总记录数的数组，[有体重反馈数, 总数]
     */
    public int[] getWeightFeedbackStats() {
        int weightFeedbackCount = userMealCheckinMapper.calculateWeightFeedbackCount();
        int totalRecords = userMealCheckinMapper.calculateTotalRecords();
        
        return new int[]{weightFeedbackCount, totalRecords};
    }
}