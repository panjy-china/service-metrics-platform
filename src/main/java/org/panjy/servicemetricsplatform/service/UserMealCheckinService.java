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
}