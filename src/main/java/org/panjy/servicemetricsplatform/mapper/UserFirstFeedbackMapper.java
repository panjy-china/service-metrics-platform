package org.panjy.servicemetricsplatform.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.panjy.servicemetricsplatform.entity.UserFirstFeedback;

import java.util.List;

@Mapper
public interface UserFirstFeedbackMapper {
    
    /**
     * 批量查询用户首次反馈
     * @param wechatId 微信ID，可为空
     * @return 用户首次反馈列表
     */
    List<UserFirstFeedback> selectBatch(@Param("wechatId") String wechatId);
    
    /**
     * 单次插入用户首次反馈
     * @param feedback 用户首次反馈
     * @return 插入记录数
     */
    int insert(UserFirstFeedback feedback);
}