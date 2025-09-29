package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.UserMealCheckin;

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
}